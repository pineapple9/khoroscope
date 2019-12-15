package com.kbtg.khoroscope.service;


import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionScopes;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.FaceAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.Vertex;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.inprocess.InProcessServerBuilder;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

public class FaceService {

    private static final String APPLICATION_NAME = "Horoscope";

    /**
     * Detects entities, sentiment, and syntax in a document using the Vision API.
     *
     * @throws Exception on errors while closing the client.
     * @throws IOException on Input/Output errors.
     */
    public static void main(String[] args) throws Exception, IOException {

//        args = new String[] { "faces"
//                , "C:\\Users\\k_horoscope99\\Documents\\software\\OpenCV\\beatiful.jpg" };
//        argsHelper(args, System.out);

//        detectFaces("C:\\Users\\k_horoscope99\\Documents\\software\\OpenCV\\beatiful.jpg");

    }

    /**
          * Helper that handles the input passed to the program.
          *
          * @throws Exception on errors while closing the client.
          * @throws IOException on Input/Output errors.
          */
    public static void argsHelper(String[] args, PrintStream out) throws Exception, IOException {
        if (args.length < 1) {
            out.println("Usage:");
            out.printf(
                    "\tmvn exec:java -DDetect -Dexec.args=\"<command> <path-to-image>\"\n"
                            + "\tmvn exec:java -DDetect -Dexec.args=\"ocr <path-to-file> <path-to-destination>\""
                            + "\n"
                            + "Commands:\n"
                            + "\tfaces | labels | landmarks | logos | text | safe-search | properties"
                            + "| web | web-entities | web-entities-include-geo | crop | ocr \n"
                            + "| object-localization \n"
                            + "Path:\n\tA file path (ex: ./resources/wakeupcat.jpg) or a URI for a Cloud Storage "
                            + "resource (gs://...)\n"
                            + "Path to File:\n\tA path to the remote file on Cloud Storage (gs://...)\n"
                            + "Path to Destination\n\tA path to the remote destination on Cloud Storage for the"
                            + " file to be saved. (gs://BUCKET_NAME/PREFIX/)\n");
            return;
        }
        String command = args[0];
        String path = args.length > 1 ? args[1] : "";

        if (command.equals("faces")) {
            detectFaces(path, out);
        }
    }

    /**
     * Connects to the Vision API using Application Default Credentials.
     */
    public static Vision getVisionService() throws IOException, GeneralSecurityException {
        GoogleCredential credential =
                GoogleCredential.getApplicationDefault().createScoped(VisionScopes.all());
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        return new Vision.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Gets up to {@code maxResults} faces for an image stored at {@code path}.
     */
    public List<FaceAnnotation> detectFaces(Path path, int maxResults) throws IOException, GeneralSecurityException {
        byte[] data = Files.readAllBytes(path);

        AnnotateImageRequest request =
                new AnnotateImageRequest()
                        .setImage(new Image().encodeContent(data))
                        .setFeatures(ImmutableList.of(
                                new Feature()
                                        .setType("FACE_DETECTION")
                                        .setMaxResults(maxResults)));

        Vision vision = getVisionService();
        
        Vision.Images.Annotate annotate = vision.images().annotate(new BatchAnnotateImagesRequest().setRequests(ImmutableList.of(request)));
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotate.setDisableGZipContent(true);

        BatchAnnotateImagesResponse batchResponse = annotate.execute();
        assert batchResponse.getResponses().size() == 1;
        AnnotateImageResponse response = batchResponse.getResponses().get(0);
        if (response.getFaceAnnotations() == null) {
            throw new IOException(
                    response.getError() != null
                            ? response.getError().getMessage()
                            : "Unknown error getting image annotations");
        }
        return response.getFaceAnnotations();
    }




    /**
     * Detects faces in the specified local image.
     *
     * @param filePath The path to the file to perform face detection on.
     * @param out A {@link PrintStream} to write detected features to.
     * @throws Exception on errors while closing the client.
     * @throws IOException on Input/Output errors.
     */
    // [START vision_face_detection]
    public static void detectFaces(String filePath, PrintStream out) throws Exception, IOException {
        List<com.google.cloud.vision.v1.AnnotateImageRequest> requests = new ArrayList<>();

        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

        com.google.cloud.vision.v1.Image img = com.google.cloud.vision.v1.Image.newBuilder().setContent(imgBytes).build();
        com.google.cloud.vision.v1.Feature feat = com.google.cloud.vision.v1.Feature.newBuilder().setType(com.google.cloud.vision.v1.Feature.Type.FACE_DETECTION).build();
        com.google.cloud.vision.v1.AnnotateImageRequest request =
                com.google.cloud.vision.v1.AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        System.out.println("ImageAnnotatorClient ==> "+ImageAnnotatorClient.create());

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            com.google.cloud.vision.v1.BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<com.google.cloud.vision.v1.AnnotateImageResponse> responses = response.getResponsesList();

            for (com.google.cloud.vision.v1.AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    out.printf("Error: %s\n", res.getError().getMessage());
                    return;
                }

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                for (com.google.cloud.vision.v1.FaceAnnotation annotation : res.getFaceAnnotationsList()) {
                    out.printf(
                            "anger: %s\njoy: %s\nsurprise: %s\nposition: %s\nLandmarks: %s",
                            annotation.getAngerLikelihood(),
                            annotation.getJoyLikelihood(),
                            annotation.getSurpriseLikelihood(),
                            annotation.getBoundingPoly(),
                            annotation.getLandmarksList());

                }
            }
        }
    }
    // [END vision_face_detection]



    //Implement
    public static List<com.google.cloud.vision.v1.FaceAnnotation.Landmark> detectFaces(String filePath) throws Exception, IOException {
        List<com.google.cloud.vision.v1.FaceAnnotation.Landmark> landmarkList = null;
        List<com.google.cloud.vision.v1.AnnotateImageRequest> requests = new ArrayList<>();

        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

        com.google.cloud.vision.v1.Image img = com.google.cloud.vision.v1.Image.newBuilder().setContent(imgBytes).build();
        com.google.cloud.vision.v1.Feature feat = com.google.cloud.vision.v1.Feature.newBuilder().setType(com.google.cloud.vision.v1.Feature.Type.FACE_DETECTION).build();
        com.google.cloud.vision.v1.AnnotateImageRequest request =
                com.google.cloud.vision.v1.AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        System.out.println("ImageAnnotatorClient ==> "+ImageAnnotatorClient.create());

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            com.google.cloud.vision.v1.BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<com.google.cloud.vision.v1.AnnotateImageResponse> responses = response.getResponsesList();

            for (com.google.cloud.vision.v1.AnnotateImageResponse res : responses) {
                if (res.hasError()) {
//                    out.printf("Error: %s\n", res.getError().getMessage());
                    System.out.println("Error: \n"+ res.getError().getMessage());
                    return null;
                }

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                for (com.google.cloud.vision.v1.FaceAnnotation annotation : res.getFaceAnnotationsList()) {
                    System.out.println("sorrow : "+annotation.getSorrowLikelihood());

                    System.out.println("anger : "+annotation.getAngerLikelihood());

                    System.out.println("joy : "+annotation.getJoyLikelihood());

                    System.out.println("surprise : "+annotation.getSurpriseLikelihood());

                    System.out.println("position : "+annotation.getBoundingPoly());

                    System.out.println("Landmarks : "+annotation.getLandmarksList());

                    System.out.println("Landmarks(0): "+annotation.getLandmarksList().get(0));
                    landmarkList = annotation.getLandmarksList();
                    break;
                }
            }
        }
        return landmarkList;
    }

    //Implement
//    public static List<com.google.cloud.vision.v1.FaceAnnotation.Landmark> detectFaceByImage(ByteString imgBytes) throws Exception, IOException {
//        List<com.google.cloud.vision.v1.FaceAnnotation.Landmark> landmarkList = null;
        public static com.google.cloud.vision.v1.FaceAnnotation detectFaceByImage(ByteString imgBytes) throws Exception, IOException {
            com.google.cloud.vision.v1.FaceAnnotation faceAnnotation = null;
        List<com.google.cloud.vision.v1.AnnotateImageRequest> requests = new ArrayList<>();

        com.google.cloud.vision.v1.Image img = com.google.cloud.vision.v1.Image.newBuilder().setContent(imgBytes).build();
        com.google.cloud.vision.v1.Feature feat = com.google.cloud.vision.v1.Feature.newBuilder().setType(com.google.cloud.vision.v1.Feature.Type.FACE_DETECTION).build();
        com.google.cloud.vision.v1.AnnotateImageRequest request =
                com.google.cloud.vision.v1.AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

//        System.out.println("ImageAnnotatorClient ==> "+ImageAnnotatorClient.create());

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            com.google.cloud.vision.v1.BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<com.google.cloud.vision.v1.AnnotateImageResponse> responses = response.getResponsesList();

            for (com.google.cloud.vision.v1.AnnotateImageResponse res : responses) {
                if (res.hasError()) {
//                    out.printf("Error: %s\n", res.getError().getMessage());
                    System.out.println("Error: \n"+ res.getError().getMessage());
                    return null;
                }

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                for (com.google.cloud.vision.v1.FaceAnnotation annotation : res.getFaceAnnotationsList()) {
//                    System.out.println("anger : "+annotation.getAngerLikelihood());
//
//                    System.out.println("join : "+annotation.getJoyLikelihood());
//
//                    System.out.println("surprise : "+annotation.getSurpriseLikelihood());

//                    System.out.println("position : "+annotation.getBoundingPoly());

//                    System.out.println("Landmarks : "+annotation.getLandmarksList());

//                    System.out.println("Landmarks(0): "+annotation.getLandmarksList().get(0));

//                    landmarkList = annotation.getLandmarksList();
                    faceAnnotation = annotation;
                    break;
                }
            }
        }
        return faceAnnotation;
    }


    public static void drawPoint(InputStream img,com.google.cloud.vision.v1.FaceAnnotation faceAnnotation) throws IOException {
        List<com.google.cloud.vision.v1.FaceAnnotation.Landmark> landmarkList = faceAnnotation.getLandmarksList();
        if(null != landmarkList) {
//        final BufferedImage image = new BufferedImage ( 1000, 1000, BufferedImage.TYPE_INT_ARGB );
//        File img = new File("C:\\Users\\k_horoscope99\\Documents\\software\\OpenCV\\test.JPG");
            final BufferedImage image = ImageIO.read(img);
            System.out.println("image ==> " + image);
            final Graphics2D graphics2D = image.createGraphics();

            int boundTopLeftX = faceAnnotation.getBoundingPoly().getVertices(0)!=null?faceAnnotation.getBoundingPoly().getVertices(0).getX():0;
            int boundTopLeftY = faceAnnotation.getBoundingPoly().getVertices(0)!=null?faceAnnotation.getBoundingPoly().getVertices(0).getY():0;
            int boundTopRightX = faceAnnotation.getBoundingPoly().getVertices(1)!=null?faceAnnotation.getBoundingPoly().getVertices(1).getX():0;
            int boundTopRightY = faceAnnotation.getBoundingPoly().getVertices(1)!=null?faceAnnotation.getBoundingPoly().getVertices(1).getY():0;
            int boundBottomRightX = faceAnnotation.getBoundingPoly().getVertices(2)!=null?faceAnnotation.getBoundingPoly().getVertices(2).getX():0;
            int boundBottomRightY = faceAnnotation.getBoundingPoly().getVertices(2)!=null?faceAnnotation.getBoundingPoly().getVertices(2).getY():0;
            int boundBottomLeftX = faceAnnotation.getBoundingPoly().getVertices(3)!=null?faceAnnotation.getBoundingPoly().getVertices(3).getX():0;
            int boundBottomLeftY = faceAnnotation.getBoundingPoly().getVertices(3)!=null?faceAnnotation.getBoundingPoly().getVertices(3).getY():0;

            int width = boundTopRightX - boundTopLeftX;
            int height = boundBottomRightY - boundTopRightY;
            System.out.println("width"+width);
            System.out.println("height"+height);
            graphics2D.setPaint ( Color.BLACK );
//            graphics2D.drawRect(boundTopLeftX,boundTopLeftY,width,height);
//            graphics2D.drawRect(boundTopRightX,boundTopRightY,0,height);
//            graphics2D.drawRect(boundBottomRightX,boundBottomRightY,0,0);
//            graphics2D.drawRect(boundBottomLeftX,boundBottomLeftY,width,0);
            graphics2D.drawLine(boundTopLeftX,boundTopLeftY,boundTopRightX,boundTopRightY);
            graphics2D.drawLine(boundTopRightX,boundTopRightY,boundBottomRightX,boundBottomRightY);
            graphics2D.drawLine(boundBottomRightX,boundBottomRightY,boundBottomLeftX,boundBottomLeftY);
            graphics2D.drawLine(boundBottomLeftX,boundBottomLeftY,boundTopLeftX,boundTopLeftY);

//        graphics2D.fillRect ( 713,535,10,10 );
            graphics2D.setPaint(Color.RED);
//        graphics2D.drawOval ( 0, 0, 1000, 1000 );
//        graphics2D.drawOval ( 713, 535, 10, 10 );
            graphics2D.drawString("หัวคิ้วซ้าย", landmarkList.get(3).getPosition().getX(), landmarkList.get(3).getPosition().getY());

            graphics2D.drawString("หัวคิ้วขวา", landmarkList.get(4).getPosition().getX(), landmarkList.get(4).getPosition().getY());

            graphics2D.dispose();

            ImageIO.write(image, "png", new File("C:\\Users\\k_horoscope99\\Documents\\software\\OpenCV\\test222.JPG"));
        }
    }

}
