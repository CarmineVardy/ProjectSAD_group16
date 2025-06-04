package it.unisa.diem.sad.geoshapes.persistence;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PersistenceService {

    private File currentFile;

    /**
     * Constructs a new {@code PersistenceService} instance.
     * Initializes the service with no current file set.
     */
    public PersistenceService() {
        this.currentFile = null;
    }

    /**
     * Retrieves the currently associated file.
     *
     * @return The {@link File} object currently associated with the service, or {@code null} if none is set.
     */
    public File getCurrentFile() {
        return currentFile;
    }

    /**
     * Sets the current file for the persistence service.
     *
     * @param currentFile The {@link File} object to be set as the current file.
     */
    public void setCurrentFile(File currentFile) {
        this.currentFile = currentFile;
    }

    public File getDirectoryName() {
        return currentFile != null ? currentFile.getParentFile() : null;
    }

    /**
     * Retrieves the name of the currently associated file.
     *
     * @return The name of the current file as a {@code String}, or {@code null} if no file is set.
     */
    public String getFileName() {
        return currentFile != null ? currentFile.getName() : null;
    }

    /**
     * Loads a drawing from the specified file.
     * The loaded content is expected to be a {@code List} of {@link MyShape} objects.
     * Sets the loaded file as the current file.
     *
     * @param file The {@link File} from which to load the drawing.
     * @return A {@code List} of {@link MyShape} objects representing the loaded drawing.
     * @throws IOException If an I/O error occurs during file reading, or if the file content is not a valid list of shapes.
     * @throws ClassNotFoundException If the class of a serialized object cannot be found.
     */
    public List<MyShape> loadDrawing(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object data = ois.readObject();
            if (!(data instanceof List<?>)) {
                if (data == null) {
                    return new ArrayList<>(); // Return an empty list if file is empty
                }
                throw new IOException("File content is not a List of Shapes.");
            }
            List<?> rawList = (List<?>) data;
            List<MyShape> shapeList = new ArrayList<>(rawList.size());
            for (Object item : rawList) {
                if (item instanceof MyShape) {
                    shapeList.add((MyShape) item);
                } else {
                    throw new IOException("File contains non-MyShape data in the list.");
                }
            }
            this.currentFile = file;
            return shapeList;
        }
    }

    /**
     * Saves the provided list of shapes to the specified file.
     * Overwrites the file if it already exists. Sets the saved file as the current file.
     *
     * @param shapes The {@code List} of {@link MyShape} objects to save.
     * @param file The {@link File} to which the drawing should be saved.
     * @throws IOException If an I/O error occurs during file writing.
     */
    public void saveDrawing(List<MyShape> shapes, File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(shapes);
            this.currentFile = file;
        }
    }



}