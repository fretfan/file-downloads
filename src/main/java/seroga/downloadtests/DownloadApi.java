package seroga.downloadtests;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DownloadApi {

  static String folder = "D:/projects/download-tests/files/";
  static String _116MB_ZIP = "116mb.zip";
  static String _2GB_ISO = "2gb.iso";

  @GetMapping("/status")
  @ResponseBody
  public String status() {
    return "all ok";
  }

  @GetMapping("/1")
  @ResponseBody
  public ResponseEntity<byte[]> downloadWithByteArray() throws IOException {
    // bad works for files size < 1MB, since file gets downloaded into memory
    var filename = _116MB_ZIP;
    Path path = Path.of(folder + filename);
    byte[] bytes = Files.readAllBytes(path);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentDisposition(ContentDisposition.builder("attachment")
        .filename(filename).build());
    return ResponseEntity.ok()
        .headers(headers)
        .body(bytes);
  }

  @GetMapping("/2")
  @ResponseBody
  public ResponseEntity<Resource> downloadWithStream() throws IOException {
    // preferred way for file size > 1mb
    var filename = _116MB_ZIP;
    Path path = Path.of(folder + filename);
    InputStreamResource resource = new InputStreamResource(new FileInputStream(path.toFile()));
    HttpHeaders headers = new HttpHeaders();
    headers.setContentDisposition(ContentDisposition.builder("attachment")
        .filename(filename).build());

    return ResponseEntity.ok()
        .headers(headers)
        .body(resource);
  }


  @GetMapping("/3")
  @ResponseBody
  public ResponseEntity<Resource> downloadClob() throws SQLException, IOException {
    String buffer = RandomStringUtils.randomAlphanumeric(1000 * 1000 * 50);
    Clob myClob = new javax.sql.rowset.serial.SerialClob(buffer.toCharArray());
    Reader reader = myClob.getCharacterStream();
    InputStream targetStream = IOUtils.toInputStream(IOUtils.toString(reader), StandardCharsets.UTF_8);
    InputStreamResource resource = new InputStreamResource(targetStream);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentDisposition(ContentDisposition.builder("attachment")
        .filename("asd.txt").build());

    reader.close();
    targetStream.close();

    return ResponseEntity.ok()
        .headers(headers)
        .body(resource);
  }

  @GetMapping("/4")
  @ResponseBody
  public ResponseEntity<Resource> downloadBlob() throws SQLException, IOException {
//    byte[] bytes = "уацкÄÖÜäõö affreb".getBytes();
    String str = RandomStringUtils.randomAlphanumeric(1000 * 1000 * 50);
    Blob blob = new javax.sql.rowset.serial.SerialBlob(str.getBytes());
    InputStream binaryStream = blob.getBinaryStream();
    InputStreamResource resource = new InputStreamResource(binaryStream);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentDisposition(ContentDisposition.builder("attachment")
        .filename("asd.txt").build());

    binaryStream.close();
//    InputStreamResource resource2 = new InputStreamResource(new ByteArrayInputStream("asd".getBytes()));
    return ResponseEntity.ok()
        .headers(headers)
        .body(resource);
  }
}
