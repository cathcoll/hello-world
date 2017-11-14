/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.flower.service;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import static sun.security.krb5.Confounder.bytes;

/**
 *
 * @author Catherine
 */
@WebService(serviceName = "FlowerService")
@Stateless()
public class FlowerService {

    private static final String[] FLOWERS = {"aster", "honeysuckle", "rose", "sunflower"};
    
    private byte[] getFlowerBytes(String name) throws IOException {
        URL resource = this.getClass().getResource("/org/flower/resources/"+name+".jpg");
        return getBytes(resource);
    }
    
    private List allFlowers() throws IOException {
        List flowers = new ArrayList();
        for (String flower:FLOWERS) {
            URL resource = this.getClass().getResource("/org/flower/resources/"+flower+".jpg");
            flowers.add(getBytes(resource));
        }
        return flowers;
    }
    /**
     * Web service operation
     * @param name
     * @return 
     * @throws java.io.IOException
     */
    @WebMethod(operationName = "getFlower")
    public Image getFlower(@WebParam(name = "name") String name) throws IOException {
        byte[] bytes = getFlowerBytes(name);
        return getImage(bytes, false);
    }

    /**
     * Web service operation
     * @return 
     * @throws java.io.IOException
     */
    @WebMethod(operationName = "getThumbnails")
    public List<Image> getThumbnails() throws IOException {
        List<byte[]> flowers = allFlowers();
        List<Image> flowerList = new ArrayList<>(flowers.size());
        for (byte[] flower : flowers) {
            flowerList.add(getImage(flower, true));
        }
        return flowerList;
    }

    private byte[] getBytes(URL resource) throws IOException {     
        InputStream in = resource.openStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        for(int read; (read = in.read(buf)) != -1;) {
            bos.write(buf, 0, read);
        }
        return bos.toByteArray();
    }

    private Image getImage(byte[] bytes, boolean isThumbnail) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        Object source = bis;
        ImageInputStream iis = ImageIO.createImageInputStream(source);
        Iterator readers = ImageIO.getImageReadersByFormatName("jpeg");
        ImageReader reader = (ImageReader) readers.next();
        ImageReadParam param = reader.getDefaultReadParam();
        if (isThumbnail) {
            param.setSourceSubsampling(4, 4, 0, 0);
        }
        reader.setInput(iis, true);
        return reader.read(0, param);    
    }
}
