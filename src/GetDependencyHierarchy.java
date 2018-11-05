import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.graph.manager.DependencyManagerUtils;
import org.eclipse.aether.util.graph.transformer.ConflictResolver;


public class GetDependencyHierarchy {


    public static RepositorySystem newRepositorySystem()

    {

        /*

         * Aether's components implement org.eclipse.aether.spi.locator.Service to ease manual wiring and using the

         * prepopulated DefaultServiceLocator, we only need to register the repository connector and transporter

         * factories.

         */

        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();

        locator.addService( RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class );

        locator.addService( TransporterFactory.class, FileTransporterFactory.class );

        locator.addService( TransporterFactory.class, HttpTransporterFactory.class );



        locator.setErrorHandler( new DefaultServiceLocator.ErrorHandler()

        {

            @Override

            public void serviceCreationFailed( Class<?> type, Class<?> impl, Throwable exception )

            {

                exception.printStackTrace();

            }

        } );



        return locator.getService( RepositorySystem.class );

    }


    public static void main(String[] args)

            throws Exception {

        System.out.println("------------------------------------------------------------");

        System.out.println(GetDependencyHierarchy.class.getSimpleName());


        RepositorySystem system = newRepositorySystem();


        DefaultRepositorySystemSession session = Booter.newRepositorySystemSession(system);


        session.setConfigProperty(ConflictResolver.CONFIG_PROP_VERBOSE, true);

        session.setConfigProperty(DependencyManagerUtils.CONFIG_PROP_VERBOSE, true);


        Artifact artifact = new DefaultArtifact("com.rabbitmq:amqp-client:5.4.3");


        ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();

        descriptorRequest.setArtifact(artifact);

        descriptorRequest.setRepositories(Booter.newRepositories(system, session));

        ArtifactDescriptorResult descriptorResult = system.readArtifactDescriptor(session, descriptorRequest);


        CollectRequest collectRequest = new CollectRequest();

        collectRequest.setRootArtifact(descriptorResult.getArtifact());

        collectRequest.setDependencies(descriptorResult.getDependencies());

        collectRequest.setManagedDependencies(descriptorResult.getManagedDependencies());

        collectRequest.setRepositories(descriptorRequest.getRepositories());

        CollectResult collectResult = system.collectDependencies(session, collectRequest);

        collectResult.getRoot().accept(new ConsoleDependencyGraphDumper(system, session));

    }


}


