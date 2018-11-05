import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.maven.repository.internal.MavenRepositorySystemUtils.newSession;

public class Booter {



    public static DefaultRepositorySystemSession newRepositorySystemSession(RepositorySystem system )

    {

        DefaultRepositorySystemSession session = newSession();



        LocalRepository localRepo = new LocalRepository( "D:\\.m2\\repository" );

        session.setLocalRepositoryManager( system.newLocalRepositoryManager( session, localRepo ) );



        session.setTransferListener( new ConsoleTransferListener() );

        session.setRepositoryListener( new ConsoleRepositoryListener() );



        // uncomment to generate dirty trees

        // session.setDependencyGraphTransformer( null );



        return session;

    }



    public static List<RemoteRepository> newRepositories(RepositorySystem system, RepositorySystemSession session )

    {

        return new ArrayList<RemoteRepository>( Arrays.asList( newCentralRepository() ) );

    }



    private static RemoteRepository newCentralRepository()

    {

        return new RemoteRepository.Builder( "central", "default", "http://central.maven.org/maven2/" ).build();

    }
}
