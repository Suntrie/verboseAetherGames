import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.util.graph.selector.AndDependencySelector;
import org.eclipse.aether.util.graph.selector.ExclusionDependencySelector;
import org.eclipse.aether.util.graph.selector.ScopeDependencySelector;
import org.eclipse.aether.util.graph.selector.StaticDependencySelector;
import org.eclipse.aether.util.graph.traverser.StaticDependencyTraverser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.maven.repository.internal.MavenRepositorySystemUtils.newSession;

public class Booter {


    public static DefaultRepositorySystemSession newRepositorySystemSession(RepositorySystem system) {

        DefaultRepositorySystemSession session = newSession();


        LocalRepository localRepo = new LocalRepository("D:\\.m2\\repository");

        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));


        DependencySelector depFilter =
                new AndDependencySelector(new ScopeDependencySelector("test"),
                         new ExclusionDependencySelector());
        session.setDependencySelector(depFilter);


        session.setDependencyTraverser(new StaticDependencyTraverser(true));

        session.setTransferListener(new ConsoleTransferListener());

        session.setRepositoryListener(new ConsoleRepositoryListener());


        // uncomment to generate dirty trees

        // session.setDependencyGraphTransformer( null );


        return session;

    }


    public static List<RemoteRepository> newRepositories(RepositorySystem system, RepositorySystemSession session) {

        return new ArrayList<RemoteRepository>(Arrays.asList(newCentralRepository()));

    }


    private static RemoteRepository newCentralRepository() {

        return new RemoteRepository.Builder("central", "default", "http://central.maven.org/maven2/").build();

    }
}
