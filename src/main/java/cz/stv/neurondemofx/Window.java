package cz.stv.neurondemofx;


import javafx.application.Application;

import javafx.beans.value.ObservableValue;
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

import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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
  private static final int MATRIX_HEIGHT = 60;

  /**
   * width of matrix
   */
  private static final int MATRIX_WIDTH = 60;

  /**
   * scale of canvas
   *
   * @see canvas
   */
  private static final double CANVAS_SCALE = 10d;

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

  /**
   * Canvas on which is showing <code>inputMatrix</code>
   *
   * @see inputMatrix
   *
   */
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

  /**
   * inputMatrix is given to Neural Network as input
   *
   * @see cz.stv.neuronnetwork
   */
  private final Image image = new Image(MATRIX_WIDTH,MATRIX_HEIGHT);
  
  private byte currentColor = 1;
  
  private boolean[][] inputMatrix = new boolean[MATRIX_WIDTH][MATRIX_HEIGHT];

  /**
   * int that indicate last coordinate of mouse.
   */
  private int lastMouseX, lastMouseY;

  private Scene scene;
  private Stage stage;
  
  private final Button buttonNext = new Button("<");
  private final Button buttonBack = new Button(">");
  
  private final Button buttonErase = new Button("Erase");
  private final Button buttonOpen = new Button("Open");
  private final Button buttonSave = new Button("Save");
  
  
  private final Label status = new Label("1/1");
  
//===================================================================================================================================================
  /**
   * <code>initScene</code> is initializing the scene.
   *
   * @param stage is current window's stage
   * @see javafx.stage
   */
  private void initScene(Stage stage)
  {
    scene = new Scene(new VBox(initCanvas(), initToolbar()));
    scene.setOnKeyPressed(this::pressed);
    scene.getRoot().setStyle("-fx-background-color: white");

    scene . getRoot().requestFocus();
    stage.setResizable(false);
    stage.setScene(scene);
    stage.setTitle("Neuron FX");
    stage.show();
  }

  private HBox initToolbar ()
  {
    HBox hBox = new HBox(10d, buttonNext , status , buttonErase , buttonOpen , buttonSave , buttonBack );
    hBox.setAlignment( Pos.CENTER );
    ObservableList<Node> content = hBox . getChildren();
    double buttonWidth = (canvas.getWidth() - hBox.getSpacing() * (content.size()-1) ) / content.size();
    
    for ( Node node : content )
    {
      if ( node instanceof Button )
      {
        Button tmp = (Button) node;
        tmp.setPrefSize( buttonWidth, 50d);
      }
      else if ( node instanceof Label)
      {
        Label tmp = (Label) node;
        tmp.setPrefSize( buttonWidth , 50d);
        tmp.setFont( new Font(20d));
        tmp.setAlignment( Pos.CENTER );
      }
    }
    
    buttonErase . setOnAction( this::erase);
    
    return hBox;
  }

//------------------------------------------------------------------------------
  /**
   * <code>initCanvas</code> is initializing the canvas.
   *
   * @see canvas
   * @see javafx.scene.canvas.Canvas
   */
  private Canvas initCanvas()
  {
    //initializing canvas
    canvas = new Canvas(CANVAS_WIDTH , CANVAS_HEIGHT);
    graphicsContext = canvas.getGraphicsContext2D();

    //setting up listeners
    canvas.setOnMousePressed(this::startPainting);
    canvas.setOnMouseReleased(this::stopPainting);
    canvas.setOnMouseDragged(this::drawMoreEfficient);
    canvas.setOnMouseExited(this::exited);
    canvas.setOnMouseEntered(this::entered);
    
    return canvas;
  }

//===================================================================================================================================================  
  
  private void startPainting(MouseEvent event)
  {
    isPainting = true;
    scene.getRoot().requestFocus();
  }

//------------------------------------------------------------------------------
  private void stopPainting(MouseEvent event)
  {
    isPainting = false;
    isOut = false;
  }

//------------------------------------------------------------------------------
  private void exited(MouseEvent event)
  {
    if (isPainting)
    {
      isPainting = false;
      isOut = true;
    }
  }

//------------------------------------------------------------------------------
  private void entered(MouseEvent event)
  {
    if (isOut)
    {
      isPainting = true;
    }
  }

//------------------------------------------------------------------------------  
  private void pressed(KeyEvent event)
  {
    KeyCode key = event.getCode();
    if (key == KeyCode.C)
    {
      erase();
    }

  }
//------------------------------------------------------------------------------
  
  private void erase(ActionEvent event)
  {
    erase();
  }
  
//------------------------------------------------------------------------------
  private void erase ()
  {
    graphicsContext.setFill(Color.WHITE);
    graphicsContext.fillRect(0 , 0 , CANVAS_WIDTH , CANVAS_HEIGHT);
    inputMatrix = new boolean[MATRIX_WIDTH][MATRIX_HEIGHT];
  }
  

//===================================================================================================================================================
  private void drawMoreEfficient(MouseEvent event)
  {
    if (lastMouseX == (int) event.getX() / CANVAS_SCALE)
    {
      return;
    }
    if (lastMouseY == (int) event.getY() / CANVAS_SCALE)
    {
      return;
    }

    if (isPainting)
    {
      int x = (int) (event.getX() / CANVAS_SCALE);
      int y = (int) (event.getY() / CANVAS_SCALE);

      if (inputMatrix[x][y] == false)
      {

        inputMatrix[x][y] = true;
        
        image.draw( (byte[][] matrix)->
        {
          matrix[x][y] = currentColor;
        });

        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(x * CANVAS_SCALE , y * CANVAS_SCALE , CANVAS_SCALE , CANVAS_SCALE);

      }
      lastMouseX = x;
      lastMouseY = y;

    }
  }

//===================================================================================================================================================  
  /**
   * Start method is called by JavaFX
   *
   * @param stage
   */
  @Override
  public void start(Stage stage)
  {
    this.stage = stage;
    initScene(stage);
  }

//===================================================================================================================================================
  /**
   * startWindow is launching JavaFX
   *
   * @param args Arguments passed by command line
   */
  public static void startWindow(String[] args)
  {
    launch(args);
  }
}
