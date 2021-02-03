package org.lean.core.metadata;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.Const;
import org.apache.hop.core.database.Database;
import org.apache.hop.core.database.DatabaseMeta;
import org.apache.hop.core.encryption.HopTwoWayPasswordEncoder;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.logging.LoggingObject;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.core.variables.Variables;
import org.apache.hop.metadata.api.HopMetadata;
import org.apache.hop.metadata.api.IHopMetadata;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.metadata.api.IHopMetadataSerializer;
import org.apache.hop.metadata.serializer.json.JsonMetadataProvider;
import org.apache.hop.metadata.serializer.memory.MemoryMetadataProvider;
import org.lean.core.LeanColorRGB;
import org.lean.core.LeanDatabaseConnection;
import org.lean.core.LeanFont;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.theme.LeanTheme;

import javax.inject.Singleton;
import javax.servlet.annotation.WebServlet;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class LeanMetadataUtil {

    private static LeanMetadataUtil leanMetadataUtil;
    public IVariables variables;
    public IHopMetadataProvider metadataProvider;


    IHopMetadataSerializer<LeanPresentation> presentationSerializer;
    IHopMetadataSerializer<LeanConnector> connectorSerializer;
    IHopMetadataSerializer<LeanDatabaseConnection> databaseSerializer;
    IHopMetadataSerializer<LeanDatabaseConnection> dbConnSerializer;
    IHopMetadataSerializer<LeanTheme> themeSerializer;


    public LeanMetadataUtil(){
        variables = Variables.getADefaultVariableSpace();
        metadataProvider = getStandardHopMetadataProvider(variables);

        createTestObjects();
    }

    public static final IHopMetadataProvider getStandardHopMetadataProvider(IVariables variables){
        String folder = variables.getVariable( Const.HOP_METADATA_FOLDER );
        if ( StringUtils.isEmpty( folder ) ) {
            // The folder is the "metadata" folder in the configuration folder...
            //
            String configDirectory = Const.HOP_CONFIG_FOLDER;
            if (!configDirectory.endsWith( Const.FILE_SEPARATOR )) {
                configDirectory+=Const.FILE_SEPARATOR;
            }
            folder=configDirectory+"metadata";
        }
//        return new JsonMetadataProvider( new HopTwoWayPasswordEncoder(), folder, variables );
        return new MemoryMetadataProvider();
    }

    public synchronized static LeanMetadataUtil getInstance(){
        if(leanMetadataUtil == null){
            leanMetadataUtil = new LeanMetadataUtil();
        }
        return leanMetadataUtil;
    }

    public static <T extends IHopMetadata> HopMetadata getHopMetadataAnnotation(Class<T> managedClass ) {
        HopMetadata hopMetadata = managedClass.getAnnotation( HopMetadata.class );
        return hopMetadata;
    }

    public IVariables getVariables(){
        return variables;
    }


    private void createTestObjects(){
        try{
            presentationSerializer = metadataProvider.getSerializer(LeanPresentation.class);
            connectorSerializer = metadataProvider.getSerializer(LeanConnector.class);
            databaseSerializer = metadataProvider.getSerializer(LeanDatabaseConnection.class);
            dbConnSerializer = metadataProvider.getSerializer(LeanDatabaseConnection.class);
            themeSerializer = metadataProvider.getSerializer(LeanTheme.class);

            createTestPresentations();
            createTestThemes();
            System.out.println("######################################################################");
            System.out.println("######################################################################");
            System.out.println("######################################################################");
            System.out.println("######################################################################");
            System.out.println("######################################################################");
            System.out.println("######################################################################");
            System.out.println("######################################################################");
            System.out.println("######################################################################");
            System.out.println("presentations: " + presentationSerializer.loadAll().size());
            System.out.println("connectors: " + connectorSerializer.loadAll().size());
            System.out.println("databases: " + databaseSerializer.loadAll().size());
            System.out.println("themes: " + themeSerializer.loadAll().size());

        }catch(HopException e){
            e.printStackTrace();
        }
    }

    private void createTestPresentations() throws HopException {
        createConnections();

        File presDir = new File("../lean-engine/src/test/resources/presentations/");
        File[] presFiles = presDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.endsWith(".json");
            }
        });

        for(File presFile : presFiles){
            try{
                FileInputStream inputStream = new FileInputStream(presFile);
                String presJSON = IOUtils.toString(inputStream);
                LeanPresentation pres = new LeanPresentation().fromJsonString(presJSON);
                List<LeanConnector> connectorList = pres.getConnectors();
                Iterator<LeanConnector> connectorIterator = connectorList.iterator();
                while(connectorIterator.hasNext()){
                    LeanConnector connector = connectorIterator.next();
                    connectorSerializer.save(connector);
                }
                List<LeanComponent> componentList = pres.getPages().get(0).getComponents();
//                Iterator<LeanComponent> componentIterator = componentList.iterator();
//                while(componentIterator.hasNext()){
//                    componentSerializer.save(componentIterator.next());
//                }

                presentationSerializer.save(pres);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void createConnections() throws HopException {
        String CONNECTOR_STEEL_WHEELS_NAME = "SteelWheels";
        LeanDatabaseConnection connection2 = new LeanDatabaseConnection("logging", "POSTGRESQL", "localhost", "5432",
                "logging", "postgres", "postgres");

        String h2DatabaseName = System.getProperty("java.io.tmpdir") + File.separator + CONNECTOR_STEEL_WHEELS_NAME;
//        connection2.setName();

        LeanDatabaseConnection swConnection = new LeanDatabaseConnection(CONNECTOR_STEEL_WHEELS_NAME,  "H2", null, null, h2DatabaseName , null, null);

        try {
            dbConnSerializer.save(connection2);
            dbConnSerializer.save(swConnection);
        } catch (HopException e) {
        }


        try {
            // Delete old database
            //
            File[] files = new File( System.getProperty( "java.io.tmpdir" ) ).listFiles(
                    new FileFilter() {
                        @Override public boolean accept( File pathname ) {
                            return pathname.toString().endsWith( ".db" ) && pathname.toString().contains( CONNECTOR_STEEL_WHEELS_NAME );
                        }
                    } );
            for (File file : files) {
                FileUtils.forceDelete(file);
            }

            // Read the script
            //
            List<String> lines = Files.readAllLines( Paths.get( "../lean-bi/src/test/resources/steelwheels/steelwheels.script" ), StandardCharsets.UTF_8 );

            DatabaseMeta databaseMeta = swConnection.createDatabaseMeta();
            Database database = new Database( new LoggingObject( swConnection.getName() ), databaseMeta );
            try {
                database.connect();

                for ( String line : lines ) {
                    database.execStatement( line );
                }

            } finally {
                database.disconnect();
            }
        }catch(Exception e) {
        }


    }

    private void createTestThemes() throws HopException {

        LeanFont theme1Font = new LeanFont("Arial", "14", false, false);
        LeanFont title1Font = new LeanFont("Arial", "20", true, true);
        LeanColorRGB t1C1 = new LeanColorRGB(255, 200, 200);
        LeanColorRGB t1C2 = new LeanColorRGB(250, 210, 210);
        LeanColorRGB t1C3 = new LeanColorRGB(180,180,180);
        LeanColorRGB t1C4 = new LeanColorRGB(200, 100, 100);

        LeanTheme theme1 = new LeanTheme();
        theme1.setName("First Theme");
        theme1.setDefaultFont(theme1Font);
        theme1.setDefaultColor(t1C1);
        theme1.setBorderColor(t1C2);
        theme1.setTitleFont(title1Font);
        theme1.setBorderColor(t1C3);
        theme1.setTitleColor(t1C4);
        theme1.setColors(Arrays.asList(t1C1, t1C2, t1C3, t1C4));

        LeanFont theme2Font = new LeanFont("Arial", "14", false, false);
        LeanFont title2Font = new LeanFont("Arial", "20", true, true);
        LeanColorRGB t2C1 = new LeanColorRGB(200, 200, 244);
        LeanColorRGB t2C2 = new LeanColorRGB(200, 210, 255);
        LeanColorRGB t2C3 = new LeanColorRGB(180,180,250);
        LeanColorRGB t2C4 = new LeanColorRGB(100, 100, 250);


        LeanTheme theme2 = new LeanTheme();
        theme2.setName("Second Theme");
        theme2.setDefaultFont(theme2Font);
        theme2.setDefaultColor(t2C1);
        theme2.setBorderColor(t2C2);
        theme2.setTitleFont(title2Font);
        theme2.setBorderColor(t2C3);
        theme2.setTitleColor(t2C4);
        theme2.setColors(Arrays.asList(t2C1, t2C2, t2C3, t2C4));

        try{
            themeSerializer.save(theme1);
            themeSerializer.save(theme2);
        }catch(HopException e){
            e.printStackTrace();
        }
    }
}
