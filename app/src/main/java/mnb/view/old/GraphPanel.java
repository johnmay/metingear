///*
// *     This file is part of Metabolic Network Builder
// *
// *     Metabolic Network Builder is free software: you can redistribute it and/or modify
// *     it under the terms of the GNU Lesser General Public License as published by
// *     the Free Software Foundation, either version 3 of the License, or
// *     (at your option) any later version.
// *
// *     Foobar is distributed in the hope that it will be useful,
// *     but WITHOUT ANY WARRANTY; without even the implied warranty of
// *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// *     GNU General Public License for more details.
// *
// *     You should have received a copy of the GNU Lesser General Public License
// *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
// */
//package mnb.view.old;
//
//import edu.uci.ics.jung.algorithms.layout.Layout;
//import edu.uci.ics.jung.algorithms.layout.SpringLayout;
//import edu.uci.ics.jung.visualization.VisualizationViewer;
//import java.awt.Dimension;
//import javax.swing.JPanel;
//
///**
// * GraphPanel.java
// * Graph panel holds the JUNG 2.1 graph visualisation server
// *
// * @author johnmay
// * @date May 27, 2011
// */
//public class GraphPanel
//        extends JPanel {
//
//    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( GraphPanel.class );
//    private Layout<String , String> layout;
//    private VisualizationViewer<String , String> viewer;
//    // storage of the graph
//    private ReactionGraph model;
//    private Boolean built = false;
//
//    public GraphPanel() {
//    }
//
//    public void setModel( ReactionGraph model ) {
//        this.model = model;
//    }
//
//    public ReactionGraph getModel() {
//        return model;
//    }
//
//    public void buildGraph() {
//
//        if ( model == null ) {
//            logger.error( "No model cannot build graph" );
//            return;
//        }
//        System.out.println( getPreferredSize() + ":" + getSize() );
//
//        layout = new SpringLayout<String , String> ( model );
//        layout.setSize( new Dimension( 700 , 500 ) );
//        viewer = new VisualizationViewer<String , String>( layout );
//        built = true;
//        viewer.setPreferredSize( new Dimension( 750 , 570 ) );
//        add( viewer );
//    }
//
//    public void reloadGrpah() {
//    }
//
//    public Boolean isBuilt() {
//        return built;
//    }
//}
