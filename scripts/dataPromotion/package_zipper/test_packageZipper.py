from fileinput import filename
import os
import unittest
import packageZipper
import shutil
from os.path import exists

class TestZipperMethods(unittest.TestCase):
    @classmethod
    def setUpClass(self):
        print('setUpClass')
        if os.path.isdir('test-folder-to-zip/'):
            shutil.rmtree('test-folder-to-zip/')
        os.mkdir('test-folder-to-zip/')
        os.mkdir('test-folder-to-zip/zip-me')
        os.mkdir('test-folder-to-zip/zip-me/zip-me-2')
    
        with open('test-folder-to-zip/zip-me/zip-me-2/bar.txt', 'a'):
            os.utime('test-folder-to-zip/zip-me/zip-me-2/bar.txt', None)

        with open('test-folder-to-zip/zip-me/foo.txt', 'a'):
            os.utime('test-folder-to-zip/zip-me/foo.txt', None)

    @classmethod
    def tearDownClass(self):
        shutil.rmtree('test-folder-to-zip/')
        os.remove('packageid_lipidomics.zip')

    def test_valid_file(self):
        filename = 'abc.test'
        EXCLUDED_TYPES = ['.jpg', 'metadata.json']
        self.assertEqual(packageZipper.is_not_excluded_type(filename, EXCLUDED_TYPES), True)

    def test_valid_file_with_no_excluded(self):
        filename = 'abc.test'
        EXCLUDED_TYPES = []
        self.assertEqual(packageZipper.is_not_excluded_type(filename, EXCLUDED_TYPES), True)

    def test_invalid_jpg(self):
        filename = 'abc.jpg'
        EXCLUDED_TYPES = ['.jpg', 'metadata.json']
        self.assertEqual(packageZipper.is_not_excluded_type(filename, EXCLUDED_TYPES), False)

    def test_invalid_metadata(self):
        filename = 'metadata.json'
        EXCLUDED_TYPES = ['.jpg', 'metadata.json']
        self.assertEqual(packageZipper.is_not_excluded_type(filename, EXCLUDED_TYPES), False)

    def test_zip_package_data(self):
        packageZipper.zip_package_data('packageid_lipidomics', 'test-folder-to-zip/')
        self.assertEqual(exists('packageid_lipidomics.zip'), True)

if __name__ == '__main__':
    unittest.main()