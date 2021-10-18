/*
 * Copyright 2021 martin.vagner.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.stv.neurondemofx;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author martin.vagner
 */
public class TestingImages
{
  public TestingImages ( Stage s )
  {
    FileChooser fCh = new FileChooser ();
    File file = fCh.showOpenDialog ( s );
    if (! file.getName () . endsWith ( ".dat" ) )
      System.out.println ( "Bad" );
    else if ( isTestingFile( file ) )
      System.out.println ( "Worse" );
            
  }

  private boolean isTestingFile ( File f )
  {
    /*try ( BufferedInputStream bf = new BufferedInputStream(new FileInputStream(f)) )
    {
      String s = new String ()
      //byte [] bytes = bf.readNBytes ( 4 );
      if ( bytes . equals ( "00000801".getBytes () ))
        System.out.println ("Equals");
    }
    catch (IOException ex)
    {
      ex.printStackTrace ();
    }*/
    
    return true;
  }
  
}
