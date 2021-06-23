package cz.stv.neurondemofx;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.application.Application;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.geometry.Pos;
import javafx.scene.Node;

import javafx.scene.Scene;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import javafx.stage.Stage;


/**
 * JavaFX <code style="color:blue">Window</code>
 *
 * @author Raferat
 */
public class Window extends Application
{

  /**
   * height of matrix
   */
  private static final int MATRIX_HEIGHT = 16;

  /**
   * width of matrix
   */
  private static final int MATRIX_WIDTH = 16;

  /**
   * scale of canvas
   *
   * @see canvas
   */
  private static final double CANVAS_SCALE = 50d;

  /**
   * width of canvas
   *
   * @see MATRIX_WIDTH
   * @see CANVAS_SCALE
   */
  private static final double CANVAS_WIDTH = MATRIX_WIDTH * CANVAS_SCALE;

  /**
   * height of canvas
   *
   * @see MATRIX_HEIGHT
   * @see CANVAS_SCALE
   */
  private static final double CANVAS_HEIGHT = MATRIX_HEIGHT * CANVAS_SCALE;

  private TextField correct;
  private TextField tresholdInput;

  private Canvas canvas;

  /**
   * graphicsContext is context of canvas.
   *
   * @see canvas
   */
  private GraphicsContext graphicsContext;

  /**
   * boolean that indicates if draw method should draw.
   *
   * @see draw
   * @see drawMoreEfficient
   * @see startPainting
   * @see stopPainting
   */
  private boolean isPainting = false;

  /**
   * boolean that indicates if mouse is out for method draw.
   *
   * @see draw
   * @see drawMoreEfficient
   * @see entered
   * @see exited
   */
  private boolean isOut = false;

  private final ImageList imageList = new ImageList( MATRIX_WIDTH , MATRIX_HEIGHT );

  private byte currentColor = 1;

  /**
   * int that indicate last coordinate of mouse.
   */
  private Scene scene;
  private Stage stage;

  private final Button buttonNext = new Button( ">" );
  private final Button buttonBack = new Button( "<" );

  private final Button buttonErase = new Button( "Erase" );
  private final Button buttonLoad = new Button( "Open" );
  private final Button buttonSave = new Button( "Save" );
  private final Button buttonAdd = new Button( "Add" );
  private final Button buttonRemove = new Button( "Delete" );
  
  private final Button buttonWhite; 

  private final Label status = new Label( "1/1" );
  
  private final int GRAY_COLOR_COUNT = 5;
  
  private final ArrayList<Button> grayButtonChoosers = new ArrayList<>();
  
  private VBox colorPicker = new VBox();

  
  
  
  public Window()
  {
    InputStream ins = Window.class.getResourceAsStream("/Erase-clean.png");
    Image img = new javafx.scene.image.Image(ins);
    
    buttonWhite = new Button("", new ImageView( img ));
  }
  
  
//===================================================================================================================================================
  /**
   * <code>initScene</code> is initializing the scene.
   *
   * @param stage is current window's stage
   * @see javafx.stage
   */
  private void initScene ( Stage stage )
  {
    scene = new Scene( new HBox( initVBox(initCanvas() , initToolbar()) , initClolorPicker() ) );
    scene.setOnKeyPressed( this :: pressed );
    scene.getRoot().setStyle( "-fx-background-color: white" );

    scene.getRoot().requestFocus();
    stage.setResizable( false );
    stage.setScene( scene );
    stage.setTitle( "Neuron FX" );
    updateControls();
    stage.show();
  }

  private HBox initToolbar ()
  {
    initButtons();
    HBox hBox = new HBox( 10d , buttonBack , status , buttonAdd , buttonRemove , buttonErase , buttonLoad , buttonSave , buttonNext );
    hBox.setAlignment( Pos.CENTER );
    ObservableList<Node> content = hBox.getChildren();
    double buttonWidth = ( canvas.getWidth() - hBox.getSpacing() * ( content.size() -1 ) ) / content.size();

    for ( Node node : content )
    {
      if ( node instanceof Button )
      {
        Button tmp = ( Button ) node;
        tmp.setPrefSize( buttonWidth , 50d );
      }
      else if ( node instanceof Label )
      {
        Label tmp = ( Label ) node;
        tmp.setPrefSize( buttonWidth , 50d );
        tmp.setFont( new Font( 20d ) );
        tmp.setAlignment( Pos.CENTER );
      }
    }

    return hBox;
  }

//------------------------------------------------------------------------------
  /**
   * <code>initCanvas</code> is initializing the canvas.
   *
   * @see canvas
   * @see javafx.scene.canvas.Canvas
   */
  private Canvas initCanvas ()
  {
    //initializing canvas
    canvas = new Canvas( CANVAS_WIDTH , CANVAS_HEIGHT );
    graphicsContext = canvas.getGraphicsContext2D();

    //setting up listeners
    canvas.setOnMousePressed( this :: startPainting );
    canvas.setOnMouseReleased( this :: stopPainting );
    canvas.setOnMouseDragged( this :: drawMoreEfficient );
    canvas.setOnMouseExited( this :: exited );
    canvas.setOnMouseEntered( this :: entered );

    return canvas;
  }
  
  
  private VBox initVBox (Node... nodes)
  {
    return new VBox(nodes);
  }

  private void initButtons ()
  {
    buttonErase.setOnAction( ( e ) ->
    {
      imageList.current().erase();
      updateControls();
      repaintEfficient();
    } );
    buttonNext.setOnAction( ( e ) ->
    {
      imageList.next();
      updateControls();
      repaintEfficient();
    } );
    buttonBack.setOnAction( ( e ) ->
    {
      imageList.back();
      updateControls();
      repaintEfficient();
    } );

    buttonSave.setOnAction( ( e ) ->
    {
      imageList.save( "/tmp/image.json" );
      updateControls();
      repaintEfficient();
    } );

    buttonLoad.setOnAction( ( e ) ->
    {
      imageList.load( "/tmp/image.json" );
      updateControls();
      repaintEfficient();
    } );

    buttonAdd.setOnAction( ( e ) ->
    {
      imageList.add();
      updateControls();
      repaintEfficient();
    } );

    buttonRemove.setOnAction( ( e ) ->
    {
      if ( imageList.position() != 0 )
      {
        imageList.delete();
        if ( imageList.position() == imageList.size() )
        {
          imageList.back();
        }
        updateControls();
        repaintEfficient();
      }
    } );
  }
  
  private VBox initClolorPicker()
  {
    grayButtonChoosers.add(buttonWhite);
    
    double gab = 10d;
    double buttonWidth = 100d;
    double buttonHeight = ( ( MATRIX_HEIGHT * CANVAS_SCALE + 50d ) - gab * (GRAY_COLOR_COUNT + grayButtonChoosers.size()-1 ) ) / ( GRAY_COLOR_COUNT + grayButtonChoosers.size() ) ;
    
    buttonWhite.setPrefSize(buttonWidth , buttonHeight);
    buttonWhite.setMinSize(buttonWidth , buttonHeight);
    buttonWhite.setMaxSize(buttonWidth , buttonHeight);
    
    ( (ImageView) buttonWhite.getGraphic()).setPreserveRatio(true);
    ( (ImageView) buttonWhite.getGraphic()).setFitHeight(buttonHeight);
    ( (ImageView) buttonWhite.getGraphic()).setFitWidth(buttonWidth);
    
    
    buttonWhite.setOnAction(this::buttonClicked);
    
    
    
    
    for ( int i = 0 ; i < GRAY_COLOR_COUNT ; i ++ )
    {
      Canvas c = new Canvas((int)buttonHeight - 10, (int)buttonHeight - 10);
      
      Color col = Color.rgb( ( 127 / GRAY_COLOR_COUNT ) * i , ( 127 / GRAY_COLOR_COUNT ) * i , ( 127 / GRAY_COLOR_COUNT ) * i );
      
      c.getGraphicsContext2D().setFill( col );
      c.getGraphicsContext2D().fillRect(0, 0, c.getWidth(), c.getHeight());
      
      Button b = new Button("" , c);
      b.setPrefSize(buttonWidth , buttonHeight);
      b.setOnAction(this::buttonClicked);
      grayButtonChoosers.add (b);
    }
    
    
    
    colorPicker = new VBox ( gab ,  grayButtonChoosers . toArray(new Button[grayButtonChoosers.size()]));
    
    
    return colorPicker;
  }
  
  private void updateColorPicker()
  {
    add();
    
    double gab = 10d;
    double buttonWidth = 100d;
    double buttonHeight = ( MATRIX_HEIGHT * CANVAS_SCALE + gab) / ( grayButtonChoosers.size() ) ;
    
    
    for ( int i = 0 ; i < grayButtonChoosers.size() ; i ++ )
    {
      Canvas c = new Canvas((int)buttonHeight - 10, (int)buttonHeight - 10);
      
      Color col = Color.rgb( ( 127 / grayButtonChoosers.size() ) * i , ( 127 / grayButtonChoosers.size() ) * i , ( 127 / grayButtonChoosers.size() ) * i );
      
      c.getGraphicsContext2D().setFill( col );
      c.getGraphicsContext2D().fillRect(0, 0, c.getWidth(), c.getHeight());
      
      Button b = new Button("" , c);
      b.setPrefSize(buttonWidth , buttonHeight);
      b.setOnAction(this::buttonClicked);
      grayButtonChoosers.set(i , b);
    }
  }
  
  private void add()
  {
    double gab = 10d;
    double buttonWidth = 100d;
    double buttonHeight = ( MATRIX_HEIGHT * CANVAS_SCALE + gab) / ( grayButtonChoosers.size() + 1 ) ;
    int newButtonIndex = grayButtonChoosers.size();
    
    Canvas c = new Canvas((int)buttonHeight - 10, (int)buttonHeight - 10);
      
    Color col = Color.rgb(( 127 / grayButtonChoosers.size()+1 ) * newButtonIndex , ( 127 / grayButtonChoosers.size()+1 ) * newButtonIndex , ( 127 / grayButtonChoosers.size()+1 ) * newButtonIndex );
      
    c.getGraphicsContext2D().setFill( col );
    c.getGraphicsContext2D().fillRect(0, 0, c.getWidth(), c.getHeight());
      
    Button b = new Button("" , c);
    b.setPrefSize(buttonWidth , buttonHeight);
    b.setOnAction(this::buttonClicked);
      
    grayButtonChoosers . add (grayButtonChoosers.size()-2 , b); 
    
    colorPicker.getChildren().add(b);
    
  }
  

//===================================================================================================================================================  
  private void startPainting ( MouseEvent event )
  {
    isPainting = true;
    scene.getRoot().requestFocus();
  }

//------------------------------------------------------------------------------
  private void stopPainting ( MouseEvent event )
  {
    isPainting = false;
    isOut = false;
  }

//------------------------------------------------------------------------------
  private void exited ( MouseEvent event )
  {
    if ( isPainting )
    {
      isPainting = false;
      isOut = true;
    }
  }

//------------------------------------------------------------------------------
  private void entered ( MouseEvent event )
  {
    if ( isOut )
    {
      isPainting = true;
    }
  }

//------------------------------------------------------------------------------  
  private void updateControls ()
  {
    updateLabel();
    updateButtons();
  }
  
  private void updateButtons()
  {
    buttonRemove . setDisable(imageList.size() == 1);
    buttonBack . setDisable(imageList.position() == 0);
    buttonNext . setDisable(imageList.position() == imageList.size()-1);
    buttonAdd . setDisable(imageList.size() == 420);
  }
  
  private void updateLabel()
  {
    String s = String.format( "%d/%d" , imageList.position()+1 , imageList.size() );
    status.setText( s );
  }

//------------------------------------------------------------------------------
  private void pressed ( KeyEvent event )
  {
    KeyCode key = event.getCode();
    if ( key == KeyCode.C )
    {
      imageList.current().erase();
      repaintEfficient();
      updateControls();
    }

  }
  
  private void buttonClicked( ActionEvent e )
  {
    if ( e.getSource() instanceof Button ) 
    {
      Button b = (Button) e.getSource();
      int index = grayButtonChoosers.indexOf(b);
      currentColor = (byte) ((127 / grayButtonChoosers.size()) * index) ;
    }
  }
//===================================================================================================================================================

  private void drawMoreEfficient ( MouseEvent event )
  {
    if ( isPainting )
    {
      int xDraw = ( int ) ( event.getX() / CANVAS_SCALE );
      int yDraw = ( int ) ( event.getY() / CANVAS_SCALE );

      imageList.current().draw( ( byte[][] matrix ) ->
      {
        matrix[ xDraw ][ yDraw ] = currentColor;
        graphicsContext.setFill( Color.WHITE );
        graphicsContext.fillRect( 0 , 0 , MATRIX_WIDTH * CANVAS_SCALE , MATRIX_HEIGHT * CANVAS_SCALE );
        

        for ( int x = 0 ; x < matrix.length ; x ++ )
        {
          for ( int y = 0 ; y < matrix[ x ].length ; y ++ )
          {
            if ( matrix[ x ][ y ] != 0 )
            {
              graphicsContext.setFill( Color.rgb(matrix[x][y], matrix[x][y], matrix[x][y]) );
              graphicsContext.fillRect( x * CANVAS_SCALE , y * CANVAS_SCALE , CANVAS_SCALE , CANVAS_SCALE );
            }
          }
        }

      } );
    }
  }

  private void repaintEfficient ()
  {
    imageList.current().draw( ( byte[][] matrix ) ->
    {
      graphicsContext.setFill( Color.WHITE );
      graphicsContext.fillRect( 0 , 0 , MATRIX_WIDTH * CANVAS_SCALE , MATRIX_HEIGHT * CANVAS_SCALE );
      
      for ( int x = 0 ; x < matrix.length ; x ++ )
      {
        for ( int y = 0 ; y < matrix[ x ].length ; y ++ )
        {
          if ( matrix[ x ][ y ] != 0 )
          {
            graphicsContext.setFill( Color.rgb(matrix[x][y], matrix[x][y], matrix[x][y]) );
            graphicsContext.fillRect( x * CANVAS_SCALE , y * CANVAS_SCALE , CANVAS_SCALE , CANVAS_SCALE );
          }
        }
      }

    } );

  }

//===================================================================================================================================================  
  /**
   * Start method is called by JavaFX
   *
   * @param stage
   */
  @Override
  public void start ( Stage stage )
  {
    this.stage = stage;
    imageList.add();
    initScene( stage );

  }

//===================================================================================================================================================
  /**
   * startWindow is launching JavaFX
   *
   * @param args Arguments passed by command line
   */
  public static void startWindow ( String[] args )
  {
    launch( args );
  }
}
