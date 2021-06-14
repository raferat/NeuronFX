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


import java.io.Reader;
import java.io.Writer;
import javafx.scene.canvas.Canvas;


/**
 *
 * @author martin.vagner
 */
public class Image
{
  private final byte[][] matrix;
  
  public Image(int width , int height)
  {
    matrix = new byte[width][height];    
  }
  
  public int getWidth()
  {
    return matrix.length;
  }
  
  public int getHeight()
  {
    return matrix[0].length;
  }
  
  
  public static Image load(Reader reader)
  {
    int width = 0;
    int height = 0;
    Image image = new Image(width,height);
    return image;
  }
  
  public void erase ()
  {
    for ( int x = 0 ; x < getWidth() ; x ++)
      for ( int y = 0 ; y < getHeight() ; y ++)
        matrix[x][y] = 0;
  }
  
  public void setPixel ( int x , int y , byte value)
  {
    matrix[x][y] = value;
  }
  
  public void save (Writer writer)
  {
    
  }
  
  public void draw(ImageDrawable imageDrawable)
  {
    imageDrawable.draw(matrix);
  }
}
