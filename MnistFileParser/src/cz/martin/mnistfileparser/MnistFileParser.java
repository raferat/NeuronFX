package cz.martin.mnistfileparser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MnistFileParser
{

  private static int position = 0;
  
  private static final LinkedList<TrainingImage> trainingImages = new LinkedList<> ();

  private final static String mnistDirectory = "R:\\P04-2021-22\\MNIST";

  private static final JLabel icon = new JLabel ();
  private static final JLabel label = new JLabel ();

  private static JFrame frame;

  private static Image readNextImage ( FileInputStream fis , int size ) throws IOException
  {
    int scale = 12;
    BufferedImage img = new BufferedImage ( size * scale , size * scale , BufferedImage.TYPE_INT_RGB );
    Graphics2D g2d = img.createGraphics ();
    byte[] read = new byte[size];
    for (int y = 0; y < size; y++)
    {
      //fis.read ( read );
      for (int x = 0; x < size; x++)
      {
        int color = 255 - fis.read ();
        g2d.setColor ( new Color ( color , color , color ) );
        g2d.fillRect ( x * scale , y * scale , 1 * scale , 1 * scale );
      }
    }

    return img;
  }

  private static int readNextLabel ( FileInputStream fis ) throws IOException
  {
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
      try (FileInputStream stream = new FileInputStream ( f ))
      {
        byte[] magicNumber = stream.readNBytes ( 4 );
        if (Arrays.equals ( magicNumber , new byte[]
        {
          0 , 0 , 8 , 3
        } ))
        {
          int max = 20;
          fromByteArray ( stream.readNBytes ( 4 ) );
          stream.readNBytes ( 4 );
          int size = fromByteArray ( stream.readNBytes ( 4 ) );
          if (trainingImages.isEmpty ())
          {
            for (int i = 0; i < max; i++)
            {
              trainingImages.add ( new TrainingImage (readNextImage ( stream , size )) );
            }
          }
          else
          {
            for ( TrainingImage t : trainingImages )
              t.setImage ( readNextImage ( stream , size ));
          }
        }
        else if (Arrays.equals ( magicNumber , new byte[]
        {
          0 , 0 , 8 , 1
        } ))
        {
          int max = 20;
          fromByteArray ( stream.readNBytes ( 4 ) );
          if (trainingImages.isEmpty ())
          {
            for (int i = 0; i < max; i++)
            {
              trainingImages.add ( new TrainingImage (readNextLabel ( stream )) );
            }
          }
          else
          {
            for ( TrainingImage t : trainingImages )
              t.setLabel ( readNextLabel ( stream ));
          }
        }

      }
      catch (IOException e)
      {
        e.printStackTrace ();
      }

    }
  }

  public static void main ( String[] args )
  {
    label.setFont ( new Font (Font.SERIF, Font.BOLD, 40));
    frame = new JFrame ( "Select input files" );
    frame.setDefaultCloseOperation ( JFrame.EXIT_ON_CLOSE );
    var d = new javax.swing.JFileChooser ( mnistDirectory );
    d.setFileSelectionMode ( JFileChooser.FILES_ONLY );
    d.setMultiSelectionEnabled ( true );
    frame.add ( d );

    frame.setSize ( 900 , 500 );
    frame.setVisible ( true );

    d.addActionListener ((ActionEvent e) ->
    {
      frame.setVisible ( false );
      frame.remove ( d );
      parse ( d.getSelectedFiles () );
      updatePosition ();
      GridLayout layout = new GridLayout (2, 1);
      JPanel j = new JPanel (layout);
      j . add ( icon );
      j . add ( label );
      frame . add ( j);
      frame . setVisible ( true );
    } );
    
    frame.addKeyListener ( new KeyAdapter ()
    {
      @Override
      public void keyPressed ( KeyEvent e )
      {
        if ( e . getKeyCode () == KeyEvent.VK_PAGE_DOWN)
        {
          position = ( position + 1 ) % trainingImages.size ();
          updatePosition ();
        }
        else if ( e . getKeyCode () == KeyEvent.VK_PAGE_UP)
        {
          position = (( position - 1 ) < 0 ? trainingImages.size () - 1 : position - 1);
          updatePosition ();
        }
      }
      
    });
    

  }

  private static void updatePosition ()
  {
    frame . setMinimumSize ( new Dimension (trainingImages.get ( 0 ) . getImage ().getWidth ( frame.getRootPane () ) , trainingImages.get ( 0 ) . getImage ().getHeight ( frame.getRootPane () ) * 2 ));
    frame . setSize ( new Dimension (trainingImages.get ( 0 ) . getImage ().getWidth ( frame.getRootPane () ) , trainingImages.get ( 0 ) . getImage ().getHeight ( frame.getRootPane () ) * 2 ));
    frame . setResizable ( false );
    icon.setIcon ( new ImageIcon (trainingImages.get(position).getImage ()));
    label.setText ( trainingImages.get(position).getLabel () + "");
  }

}
