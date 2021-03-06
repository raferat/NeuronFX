package cz.martin.mnistfileparser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MnistFileParser
{

  private static final int bufferSize = 20;
  private static int listIndex = bufferSize / 2;

  private static int imgSize = 0;

  private static final LinkedList<TrainingImage> trainingImages = new LinkedList<> ();

  private final static String mnistDirectory = "R:\\P04-2021-22\\MNIST";

  private static final JLabel icon = new JLabel ();
  private static final JLabel label = new JLabel ();

  private static JFrame frame;

  private static FileInputStream imgFis;
  private static FileInputStream labelFis;

  private static int imgFilePosition = 16;
  private static int labelFilePosition = 8;

  private synchronized static Image readNextImage ( FileInputStream fis , int size ) throws IOException
  {
    imgFilePosition += size * size * bufferSize / 2;
    fis.getChannel ().position ( imgFilePosition );
    if (fis.getChannel ().size () <= fis.getChannel ().position ())
    {
      fis.getChannel ().position ( 16 );
    }
    int scale = 12;
    BufferedImage img = new BufferedImage ( size * scale , size * scale , BufferedImage.TYPE_INT_RGB );
    Graphics2D g2d = img.createGraphics ();
    for (int y = 0; y < size; y++)
    {
      for (int x = 0; x < size; x++)
      {
        int color = 255 - fis.read ();
        g2d.setColor ( new Color ( color , color , color ) );
        g2d.fillRect ( x * scale , y * scale , 1 * scale , 1 * scale );
      }
    }

    imgFilePosition -= size * size * bufferSize / 2 - size * size;
    imgFilePosition = imgFilePosition % (16 + size * size * 60_000);

    return img;
  }

  private synchronized static Image readPreviousImage ( FileInputStream fis , int size ) throws IOException
  {
    imgFilePosition -= size * size * bufferSize / 2 + size * size;
    if (imgFilePosition < 16)
    {
      imgFilePosition = 16 + size * size * 59_999;
      //imgFilePosition -= size*size*bufferSize/2;
    }
    fis.getChannel ().position ( imgFilePosition );
    if (fis.getChannel ().size () <= fis.getChannel ().position ())
    {
      fis.getChannel ().position ( 16 + size * size * 60_000 - size * size );
    }
    int scale = 12;
    BufferedImage img = new BufferedImage ( size * scale , size * scale , BufferedImage.TYPE_INT_RGB );
    Graphics2D g2d = img.createGraphics ();
    for (int y = 0; y < size; y++)
    {
      for (int x = 0; x < size; x++)
      {
        int color = 255 - fis.read ();
        g2d.setColor ( new Color ( color , color , color ) );
        g2d.fillRect ( x * scale , y * scale , 1 * scale , 1 * scale );
      }
    }
    System.out.println ( imgFilePosition );
    imgFilePosition += size * size * bufferSize / 2 - size * size;
    if (imgFilePosition > 16 + size * size * 60_000)
    {
      imgFilePosition = 16;
    }

    return img;
  }

  private synchronized static int readNextLabel ( FileInputStream fis ) throws IOException
  {
    labelFilePosition += bufferSize / 2;
    if (fis.getChannel ().size () == fis.getChannel ().position ())
    {
      fis.getChannel ().position ( 8 );
    }
    labelFilePosition -= bufferSize / 2 + 1;
    return fis.read ();
  }

  private synchronized static int readPreviousLabel ( FileInputStream fis ) throws IOException
  {
    labelFilePosition -= bufferSize / 2;
    if (fis.getChannel ().size () == fis.getChannel ().position ())
    {
      fis.getChannel ().position ( 8 );
    }
    labelFilePosition += bufferSize / 2 - 1;
    return fis.read ();
  }

  private static int fromByteArray ( byte[] bytes )
  {
    return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
  }

  private static void parse ( File[] files )
  {
    for (File f : files)
    {
      try
      {
        FileInputStream stream = new FileInputStream ( f );
        byte[] magicNumber = stream.readNBytes ( 4 );
        if (Arrays.equals ( magicNumber , new byte[]
        {
          0 , 0 , 8 , 3
        } ))
        {
          stream.readNBytes ( 8 );
          int size = fromByteArray ( stream.readNBytes ( 4 ) );
          imgSize = size;

          if (trainingImages.isEmpty ())
          {
            for (int i = 0; i < bufferSize; i++)
            {
              trainingImages.add ( new TrainingImage ( readNextImage ( stream , size ) ) );
            }
          }
          else
          {
            for (TrainingImage t : trainingImages)
            {
              t.setImage ( readNextImage ( stream , size ) );
            }
          }

          imgFis = stream;
        }
        else if (Arrays.equals ( magicNumber , new byte[]
        {
          0 , 0 , 8 , 1
        } ))
        {
          fromByteArray ( stream.readNBytes ( 4 ) );
          if (trainingImages.isEmpty ())
          {
            for (int i = 0; i < bufferSize; i++)
            {
              trainingImages.add ( new TrainingImage ( readNextLabel ( stream ) ) );
            }
          }
          else
          {
            for (TrainingImage t : trainingImages)
            {
              t.setLabel ( readNextLabel ( stream ) );
            }
          }
          labelFis = stream;
        }

      }
      catch (IOException e)
      {
        e.printStackTrace ();
      }

    }
  }

  private static void tester ()
  {
    System.out.println ( "Started" );
    for (int i = 0; i < 60_100; i++)
    {
      System.out.println ( "Index: " + i );
      frame.dispatchEvent ( new KeyEvent ( frame , KeyEvent.KEY_PRESSED , System.nanoTime () , 0 , KeyEvent.VK_PAGE_UP ) );
    }
    System.out.println ( "Ended" );
  }

  public static void main ( String[] args )
  {
    label.setFont ( new Font ( Font.SERIF , Font.BOLD , 40 ) );
    frame = new JFrame ( "Select input files" );
    var d = new javax.swing.JFileChooser ( mnistDirectory );
    d.setFileSelectionMode ( JFileChooser.FILES_ONLY );
    d.setMultiSelectionEnabled ( true );
    frame.add ( d );

    frame.setSize ( 900 , 500 );
    frame.setVisible ( true );

    d.addActionListener ( (ActionEvent e) ->
    {
      frame.setVisible ( false );
      frame.remove ( d );
      parse ( d.getSelectedFiles () );
      updatePosition ();
      GridLayout layout = new GridLayout ( 2 , 1 );
      JPanel j = new JPanel ( layout );
      j.add ( icon );
      j.add ( label );
      frame.add ( j );
      frame.setVisible ( true );
    } );

    Thread t = new Thread (MnistFileParser::tester);
    
    frame.addKeyListener ( new KeyAdapter ()
    {
      @Override
      public void keyPressed ( KeyEvent e )
      {
        if (e.getKeyCode () == KeyEvent.VK_PAGE_DOWN)
        {
          try
          {
            listIndex++;
            updatePosition ();
            trainingImages.remove ( 0 );

            trainingImages.addLast ( new TrainingImage ( readNextImage ( imgFis , imgSize ) , readNextLabel ( labelFis ) ) );
            listIndex--;

          }
          catch (IOException ex)
          {
            ex.printStackTrace ();
          }
        }
        else if (e.getKeyCode () == KeyEvent.VK_PAGE_UP)
        {
          try
          {
            listIndex--;
            updatePosition ();
            trainingImages.removeLast ();
            trainingImages.addFirst ( new TrainingImage ( readPreviousImage ( imgFis , imgSize ) , readPreviousLabel ( labelFis ) ) );
            listIndex++;

          }
          catch (IOException ex)
          {
            ex.printStackTrace ();
          }
        }
        else if (e.getKeyCode () == KeyEvent.VK_S)
        {
          if (! t . isAlive () )
            t.start ();
        }
      }

    } );

    frame.addWindowListener ( new WindowAdapter ()
    {
      @Override
      public void windowClosing ( WindowEvent e )
      {
        try
        {
          imgFis.close ();
          labelFis.close ();
        }
        catch (IOException ex)
        {
          ex.printStackTrace ();
        }
        frame.dispose ();
      }

    } );
  }

  private static void updatePosition ()
  {
    frame.setMinimumSize ( new Dimension ( trainingImages.get ( 0 ).getImage ().getWidth ( frame.getRootPane () ) , trainingImages.get ( 0 ).getImage ().getHeight ( frame.getRootPane () ) * 2 ) );
    frame.setSize ( new Dimension ( trainingImages.get ( 0 ).getImage ().getWidth ( frame.getRootPane () ) , trainingImages.get ( 0 ).getImage ().getHeight ( frame.getRootPane () ) * 2 ) );
    frame.setResizable ( false );
    icon.setIcon ( new ImageIcon ( trainingImages.get ( listIndex ).getImage () ) );
    label.setText ( trainingImages.get ( listIndex ).getLabel () + "" );
  }

}
