package cz.martin.mnistfileparser;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MnistFileParser
{

  public static void main ( String[] args ) throws FileNotFoundException, IOException
  {
    Frame parent = new Frame();
    var d = new java.awt.FileDialog ( parent , "Input file");
    d.addWindowListener ( new WindowAdapter ()
    {
      @Override
      public void windowClosed ( WindowEvent e )
      {
        
        d.setVisible ( false);
        d . dispose ();
        parent . setVisible ( false );
        parent . dispose ();
      }
      
    });
    
    d.setVisible ( true );
    
    FileInputStream stream = new FileInputStream ( d.getDirectory () + "/" + d.getFile () );
    System.out.println (stream.available ());
    stream.close ();
    parent . dispose();
  }
  
}
