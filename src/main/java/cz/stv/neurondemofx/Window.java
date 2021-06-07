package cz.stv.neurondemofx;


//import cz.stv.neuronnetwork.InputMartixNullPointerException;
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
 * JavaFX <code style="color:blue">Window</code>
 *
 * @author Raferat
 */
public class Window extends Application
{

  /**
   * height of matrix
   */
  private static final int MATRIX_HEIGHT = 32;
  
  /**
   * width of matrix
   */
  private static final int MATRIX_WIDTH = 32;

  /**
   * scale of canvas
   * @see canvas
   */
  private static final double CANVAS_SCALE = 20d;

  /**
   * width of canvas 
   * @see MATRIX_WIDTH
   * @see CANVAS_SCALE
   */
  private static final double CANVAS_WIDTH = MATRIX_WIDTH * CANVAS_SCALE;
  
  /**
   * height of canvas 
   * @see MATRIX_HEIGHT
   * @see CANVAS_SCALE
   */
  private static final double CANVAS_HEIGHT = MATRIX_HEIGHT * CANVAS_SCALE;

  /**
   * Canvas on which is showing <code>inputMatrix</code>
   * @see inputMatrix
   * 
   */
  private Canvas canvas;
  
  /**
   * graphicsContext is context of canvas.
   * @see canvas
   */
  private GraphicsContext graphicsContext;

  /**
   * boolean that indicates if draw method should draw.
   * @see draw
   * @see drawMoreEfficient
   * @see startPainting
   * @see stopPainting
   */
  private boolean isPainting = false;
  
  /**
   * boolean that indicates if mouse is out for method draw.
   * @see draw
   * @see drawMoreEfficient
   * @see entered
   * @see exited
   */
  private boolean isOut = false;

  /**
   * inputMatrix is given to Neural Network as input
   * @see cz.stv.neuronnetwork
   */
  private boolean inputMatrix[][] = new boolean[MATRIX_WIDTH][MATRIX_HEIGHT];

  /**
   * int that indicate last coordinate of mouse.
   */
  private int lastMouseX, lastMouseY;

//===================================================================================================================================================
  /**
   * <code>initScene</code> is initializing the scene.
   * @param stage is current window's stage
   * @see javafx.stage
   */
  private void initScene(Stage stage)
  {
    Scene scene = new Scene(new StackPane(canvas));
    scene.setOnKeyPressed(this :: pressed);

    stage.setResizable(false);
    stage.setScene(scene);
    stage.setTitle("Neuron FX");
    stage.show();
  }

//------------------------------------------------------------------------------
  /**
   * <code>initCanvas</code> is initializing the canvas.
   * @see canvas
   * @see javafx.scene.canvas.Canvas
   */
  private void initCanvas()
  {
    //initializing canvas
    canvas = new Canvas(CANVAS_WIDTH , CANVAS_HEIGHT);
    graphicsContext = canvas.getGraphicsContext2D();

    //setting up listeners
    canvas.setOnMousePressed(this :: startPainting);
    canvas.setOnMouseReleased(this :: stopPainting);
    canvas.setOnMouseDragged(this :: drawMoreEfficient);
    canvas.setOnMouseExited(this :: exited);
    canvas.setOnMouseEntered(this :: entered);
  }

//===================================================================================================================================================  
  private void startPainting(MouseEvent event)
  {
    isPainting = true;
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
      graphicsContext.setFill(Color.WHITE);
      graphicsContext.fillRect(0 , 0 , CANVAS_WIDTH , CANVAS_HEIGHT);
      inputMatrix = new boolean[MATRIX_WIDTH][MATRIX_HEIGHT];
    }
  }

//===================================================================================================================================================
  private void draw(MouseEvent event)
  {
    if (isPainting)
    {
      int x = (int) (event.getX() / CANVAS_SCALE);
      int y = (int) (event.getY() / CANVAS_SCALE);

      inputMatrix[x][y] = true;

      graphicsContext.setFill(Color.BLACK);
      graphicsContext.fillRect(x * CANVAS_SCALE , y * CANVAS_SCALE , CANVAS_SCALE , CANVAS_SCALE);

    }

  }

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
    initCanvas();
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
