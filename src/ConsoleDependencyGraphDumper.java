/*******************************************************************************

 * Copyright (c) 2010, 2013 Sonatype, Inc.

 * All rights reserved. This program and the accompanying materials

 * are made available under the terms of the Eclipse Public License v1.0

 * which accompanies this distribution, and is available at

 * http://www.eclipse.org/legal/epl-v10.html

 *

 * Contributors:

 *    Sonatype, Inc. - initial API and implementation

 *******************************************************************************/



import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;

import java.util.Iterator;

import java.util.List;


import com.repoMiner.PackageUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;

import org.eclipse.aether.graph.Dependency;

import org.eclipse.aether.graph.DependencyNode;

import org.eclipse.aether.graph.DependencyVisitor;

import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.util.artifact.ArtifactIdUtils;

import org.eclipse.aether.util.graph.manager.DependencyManagerUtils;

import org.eclipse.aether.util.graph.transformer.ConflictResolver;



/**

 * A dependency visitor that dumps the graph to the console.

 */

public class ConsoleDependencyGraphDumper

        implements DependencyVisitor

{



    private PrintStream out;
    private RepositorySystem system;
    private DefaultRepositorySystemSession systemSession;



    private List<ChildInfo> childInfos = new ArrayList<ChildInfo>();



    public ConsoleDependencyGraphDumper(RepositorySystem system, DefaultRepositorySystemSession session)

    {

        this( null );

        this.system=system;
        this.systemSession=session;
    }



    public ConsoleDependencyGraphDumper( PrintStream out )

    {

        this.out = ( out != null ) ? out : System.out;

    }

    public ConsoleDependencyGraphDumper(PrintStream out, RepositorySystem system)

    {

        this.out = ( out != null ) ? out : System.out;
        this.system=system;

    }


    public boolean visitEnter( DependencyNode node )

    {

        DependencyNode winner= (DependencyNode) node.getData().get( ConflictResolver.NODE_DATA_WINNER );


        if (winner==null)
            winner=node;

        ArtifactRequest artifactRequest = new ArtifactRequest();

        artifactRequest.setArtifact( winner.getArtifact() );

        artifactRequest.setRepositories( Booter.newRepositories( system, systemSession ) );


        ArtifactResult artifactResult = null;
        try {
            artifactResult = system.resolveArtifact( systemSession, artifactRequest );
        } catch (ArtifactResolutionException e) {
            e.printStackTrace();
        }


        Artifact artifact = artifactResult.getArtifact();

        System.out.println(artifact.getArtifactId() +" - enter, "+ (winner.getDependency()==null?
                "":node.getDependency().isOptional()) +(winner.getDependency()==null?
                "":node.getDependency().getScope()));

        if (node.getChildren().size()==0)
        {


               /* PackageUtils.getLibraryClassSet("D:\\.m2\\repository\\"+winner.getArtifact().getGroupId().replace(".","\\")+"\\"
                        +winner.getArtifact().getArtifactId()+"\\"+winner.getArtifact().getVersion()+"\\"+ winner.getArtifact().getArtifactId()+"-"+winner.
                        getArtifact().getVersion()+".jar");*/
        }

      /*  out.println( formatIndentation() + formatNode( node ) );

        childInfos.add( new ChildInfo( node.getChildren().size() ) );*/

        return true;

    }



    private String formatIndentation()

    {

        StringBuilder buffer = new StringBuilder( 128 );

        for ( Iterator<ChildInfo> it = childInfos.iterator(); it.hasNext(); )

        {

            buffer.append( it.next().formatIndentation( !it.hasNext() ) );

        }

        return buffer.toString();

    }



    private String formatNode( DependencyNode node )

    {

        StringBuilder buffer = new StringBuilder( 128 );

        Artifact a = node.getArtifact();

        Dependency d = node.getDependency();

        buffer.append( a );

        if ( d != null && d.getScope().length() > 0 )

        {

            buffer.append( " [" ).append( d.getScope() );

            if ( d.isOptional() )

            {

                buffer.append( ", optional" );

            }

            buffer.append( "]" );

        }

        {

            String premanaged = DependencyManagerUtils.getPremanagedVersion( node );

            if ( premanaged != null && !premanaged.equals( a.getBaseVersion() ) )

            {

                buffer.append( " (version managed from " ).append( premanaged ).append( ")" );

            }

        }

        {

            String premanaged = DependencyManagerUtils.getPremanagedScope( node );

            if ( premanaged != null && !premanaged.equals( d.getScope() ) )

            {

                buffer.append( " (scope managed from " ).append( premanaged ).append( ")" );

            }

        }

        DependencyNode winner = (DependencyNode) node.getData().get( ConflictResolver.NODE_DATA_WINNER );

        if ( winner != null && !ArtifactIdUtils.equalsId( a, winner.getArtifact() ) )

        {

            Artifact w = winner.getArtifact();

            buffer.append( " (conflicts with " );

            if ( ArtifactIdUtils.toVersionlessId( a ).equals( ArtifactIdUtils.toVersionlessId( w ) ) )

            {

                buffer.append( w.getVersion() );

            }

            else

            {

                buffer.append( w );

            }

            buffer.append( ")" );

        }

        return buffer.toString();

    }



    public boolean visitLeave( DependencyNode node )

    {



        if ( !childInfos.isEmpty() )

        {

            childInfos.remove( childInfos.size() - 1 );

        }

        if ( !childInfos.isEmpty() )

        {

            childInfos.get( childInfos.size() - 1 ).index++;

        }

        DependencyNode winner= (DependencyNode) node.getData().get( ConflictResolver.NODE_DATA_WINNER );

        if (node.getChildren().size()!=0)
        {
            try {
                if (winner==null)
                    winner=node;

                ArtifactRequest artifactRequest = new ArtifactRequest();

                artifactRequest.setArtifact( winner.getArtifact() );

                artifactRequest.setRepositories( Booter.newRepositories( system, systemSession ) );



                ArtifactResult artifactResult = system.resolveArtifact( systemSession, artifactRequest );


                Artifact artifact = artifactResult.getArtifact();



                System.out.println(node.getArtifact().getArtifactId() +" - leave"+(winner.getDependency()==null?
                        "":node.getDependency().getScope()));
             /*   PackageUtils.getLibraryClassSet("D:\\.m2\\repository\\"+winner.getArtifact().getGroupId().replace(".","\\")+"\\"
                        +winner.getArtifact().getArtifactId()+"\\"+winner.getArtifact().getVersion()+"\\"+ winner.getArtifact().getArtifactId()+"-"+winner.getArtifact().getVersion()+".jar");
*/            } catch (ArtifactResolutionException e) {
                e.printStackTrace();
            } catch (Error e){
                e.printStackTrace();
            }
        }

        return true;

    }



    private static class ChildInfo

    {



        final int count;



        int index;



        public ChildInfo( int count )

        {

            this.count = count;

        }



        public String formatIndentation( boolean end )

        {

            boolean last = index + 1 >= count;

            if ( end )

            {

                return last ? "\\- " : "+- ";

            }

            return last ? "   " : "|  ";

        }



    }



}