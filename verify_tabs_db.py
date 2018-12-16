#!/usr/bin/env python3

import os
from xml.etree import ElementTree
import requests


def search_files(file_name, directory='.'):
    """
    Method to traverse through a given directory to find files
    """
    output = []
    file_name = file_name.lower()
    for root, _, files in os.walk(directory):
        for file in files:
            if file.startswith(file_name):
                output.append(os.path.join(root, file))
    return output


all_files = search_files('substratum_tab_')
for file in all_files:
    print("Verifying {}".format(file))
    xml_root = ElementTree.parse(file).getroot()
    for child in xml_root:
        theme_name = child.attrib.get('id')
        if not theme_name:
            raise Exception("Missing name attribute!")
        bg_link = None
        for attribute in child:
            if attribute.tag == 'backgroundimage':
                bg_link = attribute.text
        if not bg_link:
            raise Exception("Link to background image not found for {}!".format(theme_name))
        if requests.get(bg_link).status_code != 200:
            raise Exception("Invalid link specified for {}'s background image!".format(theme_name))
        print("Entry for {} is valid!".format(theme_name))
