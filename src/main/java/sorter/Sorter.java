package sorter;

import sorter.Util.Constant;
import sorter.Util.FileSorterException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Sorter {

    private Scanner scanner;
    private List<String> exclusions;

    Sorter() {
        scanner = new Scanner(System.in);
        fillListOFExclusions();
    }

    public void sort() throws FileSorterException {

        String folderPath = getFolderPath();
        File folder = new File(folderPath);

        String[] files = validateAndGetFiles(folder);

        process(files, folder.getAbsolutePath());

    }

    public String getFolderPath() throws FileSorterException {
        String path = null;
        int i = 0;
        while (i++ < Constant.ALLOWED_ATTEMPTS) {

            path = scanner.nextLine().trim();
            if (new File(path).exists())
                break;

            System.out.println(Constant.NO_FOLDER);
            path = null;
        }
        scanner.close();

        if (path == null) throw new FileSorterException(Constant.NO_FOLDER_EXC);

        return path;
    }


    public String[] validateAndGetFiles(File folder) throws FileSorterException {

        if (!folder.isDirectory())
            throw new FileSorterException(Constant.NOT_DIR);

        String[] files = folder.list();

        if (files == null || files.length == 0)
            throw new FileSorterException(Constant.EMPTY);

        return files;
    }

    public void process(String[] files, String root) {

        //new folder where subfolders will be
        String newRoot = createNewRootFolder(root);

        HashMap<String, String> map = new HashMap<>();

        mapIndexesOfOriginalFileToShortenedName(files, map);

        createFoldersByName(map, newRoot);

        moveExistingFilesToNewFolders(files, map, root, newRoot);
    }


    public String createNewRootFolder(String folderPath) {
        String sortedFolder = folderPath
                .concat(Constant.SORTED);

        File sorted = new File(sortedFolder);
        sorted.delete();
        sorted.mkdirs();

        return sortedFolder;
    }

    private void moveExistingFilesToNewFolders(String[] files, HashMap<String, String> map, String root, String newRoot) {

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String[] indexes = entry.getValue().split(Constant.DELIM);
            Arrays.stream(indexes).forEach(index ->
            {
                try {
                    Path current = Path.of(root.concat(Constant.SLSH).concat(files[Integer.valueOf(index)]));
                    Path destination = Path.of(
                            newRoot
                                    .concat(Constant.SLSH)
                                    .concat(entry.getKey())
                                    .concat(Constant.SLSH)
                                    .concat(files[Integer.valueOf(index)]));

                    Files.copy(current, destination);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

    }

    private void createFoldersByName(HashMap<String, String> map, String root) {

        for (Map.Entry<String, String> entry : map.entrySet()) {
            File newFolder = new File(root.concat(Constant.SLSH).concat(entry.getKey()));
            newFolder.mkdirs();
        }
    }


    private void mapIndexesOfOriginalFileToShortenedName(String[] files, HashMap<String, String> map) {
        for (int i = 0; i < files.length; i++) {
            String cleaned = removeExclusionsAndExtension(files[i].toLowerCase());
            String indexes = map.get(cleaned);
            map.put(cleaned, indexes == null ?
                    String.valueOf(i).concat(Constant.DELIM) : indexes.concat(String.valueOf(i).concat(Constant.DELIM)));
        }
    }


    public String removeExclusionsAndExtension(String name) {

        //remove file extention
        int index = name.indexOf(".");
        name = index == -1 ? name : name.substring(0, index);

        //remove not needed words
        for (String word : exclusions)
            if (name.contains(word))
                name = name
                        .replace(word, Constant.EMPTY_STR)
                        .trim();

        //get first 8 letters - street number & beginning of name - enough for address id
        return name.substring(0, name.length() < Constant.NAME_LENGTH ? name.length() : Constant.NAME_LENGTH).trim();

    }


    public void fillListOFExclusions() {
        this.exclusions = new ArrayList<>();
        exclusions.add("lease");
        exclusions.add("agreement");
        exclusions.add("extension");
        exclusions.add("commercial");
        exclusions.add("signed");
    }

}
