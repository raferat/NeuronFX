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


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ImageList
{
  private final ArrayList<Image> images = new ArrayList<>();
  
  private int currentIndex = 0;
  
  private final int imageWidth;
  private final int imageHeight;
  
  public ImageList(int imageWidth , int imageHeight)
  {
    this.imageWidth = imageWidth;
    this.imageHeight = imageHeight;
  }
  
  public boolean empty()
  {
    return images.isEmpty();
  }
  
  
  public int position()
  {
    return currentIndex;
  }
  
  public int size()
  {
    return images.size();
  }
  
  public Image current ()
  {
    if (! empty() )
      return images.get( currentIndex );
    else 
      return null;
  }
  
  
  public void add ()
  {
    Image image = new Image(imageWidth , imageHeight);
    add(image);
  }
  
  private void add (Image image)
  {
    images.add( image );
    currentIndex = images.size()-1;
  }
  
  public void delete ()
  {
    images.remove( currentIndex );
  }
  
  
  public void save(File file)
  {
    try (FileWriter writer = new FileWriter(file))
    {
      if (! file.exists() )
        file.createNewFile();
      
      current().save(writer);
    }
    catch (IOException ex)
    {
      ex.printStackTrace(System.err);
    }
  }
  
  public void save ( String filePath )
  {
    save(new File(filePath));
  }
  
  public void load(String filePath)
  {
    load(new File(filePath));
  }
  
  public void load ( File file )
  {
    try(FileReader reader = new FileReader(file))
    {
      if ( file.exists() )
      {
        add(Image.load(reader));
      }  
    }
    catch (IOException e)
    {
      e.printStackTrace(System.err);
    }
  }
  
  public void back()
  {
    currentIndex = currentIndex == 0 ? size()-1 : currentIndex-1;
  }
  
  public void next()
  {
    currentIndex = currentIndex == size() -1 ? 0 : currentIndex +1;
  }
  
}
