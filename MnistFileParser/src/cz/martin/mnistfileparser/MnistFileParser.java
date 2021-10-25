package cz.martin.mnistfileparser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MnistFileParser
{

  private final static String mnistDirectory = "R:\\P04-2021-22\\MNIST";

  private static JLabel icon = new JLabel ();
  private static JLabel label = new JLabel ();
  
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

  private static String readNextLabel ( FileInputStream fis ) throws IOException
  {
    return "" + fis.read ();
  }

  private static int fromByteArray ( byte[] bytes )
  {
    int result = 0;
    for (int i = 0; i < bytes.length; i++)
    {
      result += bytes[i] * Math.pow ( 256 , bytes.length - i - 1 );
    }
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
          stream.readNBytes ( 8 );
          icon = new JLabel ( new ImageIcon ( readNextImage ( stream , fromByteArray ( stream.readNBytes ( 4 ) ) ) ) );
          frame.add ( icon );
          
        }
        else if (Arrays.equals ( magicNumber , new byte[]
        {
          0 , 0 , 8 , 1
        } ))
        {
          stream.readNBytes ( 4 );
          label . setText ( readNextLabel ( stream));
          frame . add ( label );
        }

      }
      catch (IOException e)
      {
        e.printStackTrace ();
      }

    }
    frame.setVisible ( true );
  }

  public static void main ( String[] args )
  {
    frame = new JFrame ( "Select input files" );
    frame . setLayout ( new BoxLayout (frame.getContentPane (), BoxLayout.Y_AXIS));
    frame.setDefaultCloseOperation ( JFrame.EXIT_ON_CLOSE );
    var d = new javax.swing.JFileChooser ( mnistDirectory );
    d.setFileSelectionMode ( JFileChooser.FILES_ONLY);
    d . setMultiSelectionEnabled(true);
    frame.add ( d );

    frame.setSize ( 900 , 500 );
    frame.setVisible ( true );

    d.addActionListener ( (ActionEvent e) ->
    {
      frame.setVisible ( false );
      frame.remove ( d );
      parse ( d.getSelectedFiles () );
    } );

  }

}
