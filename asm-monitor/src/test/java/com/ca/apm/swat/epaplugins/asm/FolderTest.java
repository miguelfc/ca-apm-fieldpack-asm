package com.ca.apm.swat.epaplugins.asm;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for testing the fldr_get API.
 * 
 * @author Guenter Grossberger - CA APM SWAT Team
 *
 */
public class FolderTest extends FileTest {


    /**
     * Test getFolders() without any properties.
     */
    @Test
    public void getFoldersSimple() {

        try {
            // set properties
            AsmReader.getProperties().setProperty(INCLUDE_FOLDERS, ALL_FOLDERS);
            AsmReader.getProperties().setProperty(EXCLUDE_FOLDERS, EMPTY_STRING);

            // call API
            String[] folderList = requestHelper.getFolders();

            // folderList should contain those entries
            String[] expectedFolders = {
                "root_folder",
                "APM Vendor Sites",
                "Caterpillar",
                "Customer Sites",
                "NML",
                "OPMS Testing",
                "Test folder name length",
                "Tests",
                "Web Service tests"
            };

            // check
            checkMetrics(expectedFolders, folderList);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test getFolders() with include property.
     */
    @Test
    public void getFoldersInclude() {

        try {
            // folderList should contain those entries
            String[] expectedFolders = {
                "APM Vendor Sites",
                "Caterpillar",
                "Customer Sites",
                "NML",
                "OPMS Testing",
                "Tests"
            };

            // create include property
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < expectedFolders.length; ++i) {
                if (i > 0) {
                    buf.append(',');
                }
                buf.append(expectedFolders[i]);
            }

            // set properties
            AsmReader.getProperties().setProperty(INCLUDE_FOLDERS, buf.toString());
            AsmReader.getProperties().setProperty(EXCLUDE_FOLDERS, EMPTY_STRING);

            // call API
            String[] folderList = requestHelper.getFolders();

            // check
            checkMetrics(expectedFolders, folderList);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test getFolders() with exclude property.
     */
    @Test
    public void getFoldersExclude() {

        try {
            // folderList should contain those entries
            String[] expectedFolders = {
                "root_folder",
                "APM Vendor Sites",
                "Caterpillar",
                "Customer Sites",
                "NML",
                "Tests"
            };

            String[] excludedFolders = {
                "OPMS Testing",
                "Test folder name length",
                "Web Service tests"
            };

            // create exclude property
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < excludedFolders.length; ++i) {
                if (i > 0) {
                    buf.append(',');
                }
                buf.append(excludedFolders[i]);
            }

            // set properties
            AsmReader.getProperties().setProperty(INCLUDE_FOLDERS, EMPTY_STRING);
            AsmReader.getProperties().setProperty(EXCLUDE_FOLDERS, buf.toString());

            // call API
            String[] folderList = requestHelper.getFolders();

            // check
            checkMetrics(expectedFolders, folderList);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}