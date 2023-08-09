package sorter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sorter.Util.Constant;
import sorter.Util.FileSorterException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Scanner;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SorterTest {

    @Mock
    Scanner testScanner;


    @Test
    void testUserPathInput() throws IllegalAccessException, FileSorterException {
        String givenPath = createTopEmptyFolder();

        Sorter sorter = new Sorter();
        Field field = ReflectionUtils
                .findFields(Sorter.class, f -> f.getName().equals("scanner"),
                        ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
                .get(0);
        field.setAccessible(true);
        field.set(sorter, testScanner);
        when(testScanner.nextLine()).thenReturn(givenPath);
        String path = sorter.getFolderPath();

        Assertions.assertEquals(givenPath, path);
    }

    private String createTopEmptyFolder() {
        String givenPath = "src/test/testFolder/test";
        File test = new File(givenPath);
        test.delete();
        Assertions.assertEquals(true, test.mkdirs());
        return givenPath;
    }

    @Test
    void testUserPathWrongInput() throws IllegalAccessException {
        Sorter sorter = new Sorter();
        Field field = ReflectionUtils
                .findFields(Sorter.class, f -> f.getName().equals("scanner"),
                        ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
                .get(0);
        field.setAccessible(true);
        field.set(sorter, testScanner);
        when(testScanner.nextLine()).thenReturn("broken").thenReturn("broken").thenReturn("broken").thenReturn("broken");

        Assertions.assertThrows(FileSorterException.class, () -> sorter.getFolderPath());
    }

    @Test
    void testFileInsteadOfFolder() throws IOException {

        String givenPath = "src/test/testFolder/test";
        File given = new File(givenPath);
        given.delete();
        given.createNewFile();
        Assertions.assertEquals(true, given.exists());

        Sorter sorter = new Sorter();

        Assertions.assertThrows(FileSorterException.class, () -> sorter.validateAndGetFiles(given));
    }

    @Test
    void testEmptyFolder() {

        String givenPath = createTopEmptyFolder();

        Sorter sorter = new Sorter();

        Assertions.assertThrows(FileSorterException.class, () -> sorter.validateAndGetFiles(new File(givenPath)));
    }


    @Test
    void testNewRootFolderCreated() throws IOException {

        String givenPath = "src/test/testFolder/test";

        Sorter sorter = new Sorter();
        sorter.createNewRootFolder(givenPath);

        Assertions.assertEquals(true, new File(givenPath + Constant.SORTED).exists());

        new File(givenPath + Constant.SORTED).delete();
    }

    @Test
    void testOneNewFolderTwoFilesProcess() throws IOException {

        String givenPath = "src/test/testFolder/test";
        String testFile = "/1234 Main.txt";
        String testFile2 = "/1234 Main Renewal.txt";
        new File(givenPath + testFile).createNewFile();
        new File(givenPath + testFile2).createNewFile();

        String expectedPath = givenPath + Constant.SORTED;
        File expected = new File(expectedPath);
        expected.delete();
        Assertions.assertEquals(false, new File(expectedPath).exists());

        Sorter sorter = new Sorter();
        sorter.process(new String[]{testFile, testFile2}, givenPath);

        File one = new File(expected + "/1234 Ma" + testFile);
        File two = new File(expected + "/1234 Ma" + testFile2);

        Assertions.assertEquals(true, one.exists());
        Assertions.assertEquals(true, two.exists());

        one.delete();
        two.delete();
        new File(expected + "/1234 Ma").delete();
        expected.delete();
    }

    @Test
    void removingExclusionsFromNames() {

        Sorter sorter = new Sorter();

        String[] test = {"123 main lease", "123 qweqwe", "lease 456 new.txt", "123 zxc.pdf", "123 main 23 signed"};

        String[] expected = {"123 main", "123 qweq", "456 new", "123 zxc", "123 main"};

        for (int i = 0; i < test.length; i++)
            Assertions.assertEquals(expected[i], sorter.removeExclusionsAndExtension(test[i]));

    }


    @Test
    void testTwoFoldersTwoFilesProcess() throws IOException {

        String givenPath = "src/test/testFolder/test";
        String testFile = "/123 Main.txt";
        String testFile2 = "/456 North.txt";
        new File(givenPath + testFile).createNewFile();
        new File(givenPath + testFile2).createNewFile();

        String expectedPath = givenPath + Constant.SORTED;
        File expected = new File(expectedPath);
        expected.delete();
        Assertions.assertEquals(false, new File(expectedPath).exists());

        Sorter sorter = new Sorter();
        sorter.process(new String[]{testFile, testFile2}, givenPath);

        File one = new File(expected + "/123 Mai" + testFile);
        File two = new File(expected + "/456 Nor" + testFile2);

        Assertions.assertEquals(true, one.exists());
        Assertions.assertEquals(true, two.exists());

        one.delete();
        two.delete();
        new File(expected + "/123 Ma").delete();
        new File(expected + "/456 Nor").delete();
        expected.delete();
    }


}
