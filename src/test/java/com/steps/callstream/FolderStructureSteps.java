package com.steps.callstream;

import com.data.IOPath;
import com.x5.template.Chunk;
import com.x5.template.Theme;
import cucumber.api.DataTable;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.After;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import com.processing.CallStream;
import org.apache.commons.io.FileUtils;

import static junit.framework.Assert.assertTrue;

public class FolderStructureSteps {

    private IOPath ioPath;

    public FolderStructureSteps(IOPath ioPath) {
        this.ioPath = ioPath;
    }

    @Given("^a file with theme (calls_log) containing the following (lines)$")
    public void aFileContainingTheFollowingLines(String themeName, String dataPoint, DataTable table) throws Throwable {

        List<Map<String,String>> test_lines = table.asMaps(String.class, String.class);

        Chunk lines = getTemplate(themeName + "#" + dataPoint);
        lines.set(dataPoint, test_lines);

        this.ioPath.input_path = writeDataToFile("data.dat", lines.toString().getBytes());
    }

    @When("^I run the CallStream job$")
    public void iRunTheCallStreamJob() throws Throwable {
        this.ioPath.output_path = "output/callstream/";

        String[] arguments = {this.ioPath.input_path, this.ioPath.output_path};
        CallStream.main(arguments);
    }

    @Then("^the following directory structure should be created:")
    public void theFollowingDirectoryStructureShouldBeCreated(List<String> table) throws Throwable {
       for (String m : table) {
           assertTrue(String.format("Output directory `%s` does not exist",
                   Paths.get(this.ioPath.output_path, m).toString()),
                   Files.exists(Paths.get(this.ioPath.output_path, m)));
        }
     }

    @Then("folder '(.+)' should contain following files(?: too)?:$")
    public void directoryShouldContainFile(String folder, List<String> table) throws Throwable {
        for (String m : table) {
            assertTrue(String.format("Output file `%s` does not exist",
                    Paths.get(this.ioPath.output_path, folder, m).toString()),
                    Files.exists(Paths.get(this.ioPath.output_path, folder, m)));
        }
    }

    @After
    public void cleanupOutputFolder() throws Throwable  {
//        FileUtils.deleteQuietly(new File(this.ioPath.input_path));
//        FileUtils.deleteDirectory(new File(this.ioPath.output_path));
    }

    /**
     * Write given bytes to file
     *
     * @return absolute path to created file
     */
    private static String writeDataToFile(String filename, byte[] data) throws IOException {

        File input = new File(filename);
        PrintStream out = new PrintStream(input);

        out.write(data);
        out.close();

        return input.getAbsolutePath();
    }

    /**
     * Get template to render values in.
     *
     * @return Chunk
     */
    private static Chunk getTemplate(String templateSnippet) {
        Theme theme = new Theme();
        return theme.makeChunk(templateSnippet);
    }
}
