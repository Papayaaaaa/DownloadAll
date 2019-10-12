package com.example.javacore.download;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


public class DownloadAll {
    static void DownloadAll(String urlfilepath,String savePath) throws IOException{
        FileInputStream inputStream = new FileInputStream(urlfilepath);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String str = null;
        while((str = bufferedReader.readLine()) != null)
        {
            System.out.println(str);
            //将url中的时间当作名字
            String filename="TRMM-V7-"+str.split("[.]")[6]+".nc";
            DownloadFromUrl(str,filename,savePath);
        }
        //close
        inputStream.close();
        bufferedReader.close();

    };
    static void DownloadFromUrl(String urlstr,String fileName,String savePath)throws IOException{
        URL url=new URL(urlstr);
        HttpURLConnection conn=(HttpURLConnection) url.openConnection();  ;
        String cookies = "JSESSIONID=3F90C734BF3C587C68297D0DC9AFBEDD; _ga=GA1.2.2045210685.1554170340; nasa_gesdisc_data_archive=jMKf41TcQROtZ4Xbg0ZclbKWpAL6plPqFp/bkb/2BOEOKnwaUVd1q2lWLArerYAccF7BvnQBs7EsUulFMPjwHj8uL08MODevUwYAqv2dYPUp9PcWVTOlv0OM5O/cbTGI; _gid=GA1.2.1435419156.1570773780";
        conn.setRequestMethod("GET");
        //必须设置false，否则会自动redirect到重定向后的地址
        conn.setInstanceFollowRedirects(false);
        conn.addRequestProperty("Accept-Charset", "UTF-8;");
        conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
        conn.addRequestProperty("Referer", "http://disc2.gesdisc.eosdis.nasa.gov/");
        conn.connect();

        //判定是否会进行302重定向
        while (conn.getResponseCode() == 302) {
            //如果会重定向，保存302重定向地址，以及Cookies,然后重新发送请求(模拟请求)
            String location = conn.getHeaderField("Location");
            //设置用户名和密码并用BASE64编码
            String userPassword = "papaya" + ":" + "Lx1019424650";
            String encoding=new sun.misc.BASE64Encoder().encode (userPassword.getBytes());
            url = new URL(location);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("Cookie", cookies);
            conn.addRequestProperty("Accept-Charset", "UTF-8;");
            conn.addRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
            conn.addRequestProperty("Referer", "http://disc2.gesdisc.eosdis.nasa.gov/");
            conn.setRequestProperty("Host","disc2.gesdisc.eosdis.nasa.gov");
            //设置用户名和密码
            conn.setRequestProperty("Upgrade-Insecure-Requests","1");
            conn.setRequestProperty ("Authorization", "Basic " + encoding);
            conn.connect();
            System.out.println("跳转地址:" + location);
        }

        InputStream inputStream =conn.getInputStream();
        byte[] getData = readInputStream(inputStream);

        //文件保存位置
        File saveDir = new File(savePath);
        if(!saveDir.exists()){
            saveDir.mkdirs();
        }
        File file = new File(saveDir+File.separator+fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if(fos!=null){
            fos.close();
        }
        if(inputStream!=null){
            inputStream.close();
        }

        System.out.println("info:"+url+" download success");
    };

    public static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }


    public static void main(String[] args)throws IOException {
        DownloadAll("G:\\TRMM V7\\subset_TRMM_3B42_Daily_7_20191011_070521.txt","G:\\TRMM V7");
    };

}
