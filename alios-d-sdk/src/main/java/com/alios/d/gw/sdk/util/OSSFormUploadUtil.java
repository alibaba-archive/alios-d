package com.alios.d.gw.sdk.util;

import com.alios.d.gw.sdk.dto.ResultCodes;
import com.alios.d.gw.sdk.dto.ResultDTO;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

public class OSSFormUploadUtil {
    /**
     * 上传文件到oss
     *
     * @param formFields
     * @param host
     * @param localFilePath
     * @return
     */
    public static ResultDTO formUpload(Map<String, String> formFields, String host, String localFilePath) {
        String res = "";
        HttpURLConnection conn = null;
        String boundary = "9431149156168";

        try {
            URL url = new URL(host);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);
            OutputStream out = new DataOutputStream(conn.getOutputStream());

            // text
            if (formFields != null) {
                StringBuffer strBuf = new StringBuffer();
                Iterator<Map.Entry<String, String>> iter = formFields.entrySet().iterator();
                int i = 0;

                while (iter.hasNext()) {
                    Map.Entry<String, String> entry = iter.next();
                    String inputName = entry.getKey();
                    String inputValue = entry.getValue();

                    if (inputValue == null) {
                        continue;
                    }

                    if (i == 0) {
                        strBuf.append("--").append(boundary).append("\r\n");
                        strBuf.append("Content-Disposition: form-data; name=\""
                                + inputName + "\"\r\n\r\n");
                        strBuf.append(inputValue);
                    } else {
                        strBuf.append("\r\n").append("--").append(boundary).append("\r\n");
                        strBuf.append("Content-Disposition: form-data; name=\""
                                + inputName + "\"\r\n\r\n");
                        strBuf.append(inputValue);
                    }

                    i++;
                }
                out.write(strBuf.toString().getBytes());
            }

            // file
            File file = new File(localFilePath);
            String filename = file.getName();
            String contentType = new MimetypesFileTypeMap().getContentType(file);
            if (contentType == null || contentType.equals("")) {
                contentType = "application/octet-stream";
            }

            StringBuffer strBuf = new StringBuffer();
            strBuf.append("\r\n").append("--").append(boundary)
                    .append("\r\n");
            strBuf.append("Content-Disposition: form-data; name=\"file\"; "
                    + "filename=\"" + filename + "\"\r\n");
            strBuf.append("Content-Type: " + contentType + "\r\n\r\n");

            out.write(strBuf.toString().getBytes());

            DataInputStream in = new DataInputStream(new FileInputStream(file));
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            in.close();

            byte[] endData = ("\r\n--" + boundary + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();

            // Gets the file data
            strBuf = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuf.append(line).append("\n");
            }
            res = strBuf.toString();
            reader.close();
            reader = null;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getResult(false, ResultCodes.OSS_UPLOAD_FAILED);
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
        return ResultDTO.getResult(true);
    }
}
