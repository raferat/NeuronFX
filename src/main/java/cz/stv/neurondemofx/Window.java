package cz.stv.neurondemofx;


import cz.stv.neuronnetwork.InputMartixNullPointerException;
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
 * JavaFX Window <code>Window</code>
 *
 * @author Raferat
 */
public class Window extends Application
{

  private static final int MATRIX_HEIGHT = 32;
  private static final int MATRIX_WIDTH = 32;

  private static final double CANVAS_SCALE = 20d;

  private static final double CANVAS_WIDTH = MATRIX_WIDTH * CANVAS_SCALE;
  private static final double CANVAS_HEIGHT = MATRIX_HEIGHT * CANVAS_SCALE;

  private Canvas canvas;
  private GraphicsContext graphicsContext;

  private boolean isPainting = false;
  private boolean isOut = false;

  private boolean inputMatrix[][] = new boolean[MATRIX_WIDTH][MATRIX_HEIGHT];

  private int lastMouseX, lastMouseY;

//===================================================================================================================================================  
  private void initScene(Stage stage)
  {
    Scene scene = new Scene(new StackPane(canvas));
    scene.setOnKeyPressed((event) -> pressed(event));

    stage.setResizable(false);
    stage.setScene(scene);
    stage.setTitle("Neuron FX");
    stage.show();
  }

//------------------------------------------------------------------------------
  private void initCanvas()
  {
    //initializing canvas
    canvas = new Canvas(CANVAS_WIDTH , CANVAS_HEIGHT);
    graphicsContext = canvas.getGraphicsContext2D();

    //setting up listeners
    canvas.setOnMousePressed((event) -> startPainting());
    canvas.setOnMouseReleased((event) -> stopPainting());
    canvas.setOnMouseDragged((event) -> drawEfficient(event));
    canvas.setOnMouseExited((event) -> exited());
    canvas.setOnMouseEntered((event) -> entered());
  }

//===================================================================================================================================================  
  private void startPainting()
  {
    isPainting = true;
  }

//------------------------------------------------------------------------------
  private void stopPainting()
  {
    isPainting = false;
    isOut = false;
  }

//------------------------------------------------------------------------------
  private void exited()
  {
    if (isPainting)
    {
      isPainting = false;
      isOut = true;
    }
  }

//------------------------------------------------------------------------------
  private void entered()
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

  private void drawEfficient(MouseEvent event)
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
