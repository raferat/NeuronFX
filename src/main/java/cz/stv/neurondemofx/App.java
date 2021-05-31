package cz.stv.neurondemofx;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


/**
 * JavaFX App
 */
public class App extends Application
{
  private static final int MATRIX_HEIGHT = 32;
  private static final int MATRIX_WIDTH = 32;
  
  private static final double CANVAS_SCALE = 40;
  
  private static final double CANVAS_WIDTH = MATRIX_WIDTH * CANVAS_SCALE;
  private static final double CANVAS_HEIGHT = MATRIX_HEIGHT * CANVAS_SCALE;

  private final Canvas canvas = new Canvas(CANVAS_WIDTH , CANVAS_HEIGHT);
  private GraphicsContext graphicsContext;
  
  private boolean isPainting = false;
  
  private boolean inputMatrix[][] = new boolean[MATRIX_WIDTH][MATRIX_HEIGHT];
  
  private void startPainting ()
  {
    isPainting = true;
  }
  
  private void stopPainting()
  {
    isPainting = false;
  }
  
  private void draw ( MouseEvent event )
  {
    if ( isPainting )
    {
      int x = ( int ) ( event . getX() / CANVAS_SCALE );
      int y = ( int ) ( event . getY() / CANVAS_SCALE );
      
      inputMatrix[x][y] = true;
      
      graphicsContext . fillRect( x * CANVAS_SCALE , y * CANVAS_SCALE, CANVAS_SCALE, CANVAS_SCALE);
      
      
      
    }
    
    //System . out . println ( "Drag" );
  }

  private void pressed(KeyEvent event)
  {
    KeyCode key = event . getCode();
    if ( key == KeyCode . C )
    {
      graphicsContext.setFill( Color.WHITE);
      graphicsContext.fillRect( 0 , 0 , CANVAS_WIDTH, CANVAS_HEIGHT );
      inputMatrix = new boolean[MATRIX_WIDTH][MATRIX_HEIGHT];
    }
  }
  
  
  
  @Override
  public void start ( Stage stage )
  { 
    graphicsContext = canvas.getGraphicsContext2D();
    graphicsContext.setFill(Color.BLACK);
    canvas . setOnMousePressed((event) -> startPainting());
    canvas . setOnMouseReleased((event) -> stopPainting() );
    canvas . setOnMouseDragged( (event) -> draw (event) );
    canvas . setOnMouseExited( (event) -> stopPainting());
    
    
    Scene scene = new Scene( new StackPane( canvas ));
    scene . setOnKeyPressed( (event) -> pressed(event) );
    
    stage.setResizable( false );
    stage.setScene( scene );
    stage.setTitle( "Neuron FX" );
    stage.show();
    stage.setOnCloseRequest( (event) -> 
    {
      for ( int x = 0 ; x < MATRIX_WIDTH ; x ++ )
      {
        for ( int y = 0 ; y < MATRIX_HEIGHT ; y ++ )
        {
          System . out . printf ( inputMatrix [x] [y] ? "O" : " " );
        }
        System . out . println("");
      }
      
    });
  }

  public static void main ( String[] args )
  {
    launch();
  }
}
