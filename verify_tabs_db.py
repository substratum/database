#!/usr/bin/env python3

import os
from xml.etree import ElementTree

import play_scraper
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


def verify_price(package):
    try:
        return play_scraper.details(package)['free']
    except ValueError as e:
        print(e)
        return "Not found"


def main():
    all_files = search_files('substratum_tab_')
    for file in all_files:
        print("Verifying {}".format(file))
        xml_root = ElementTree.parse(file).getroot()
        for child in xml_root:
            theme_name = child.attrib.get('id')
            if not theme_name:
                raise Exception("Missing name attribute!")
            bg_link, package_name, price = None, None, None
            for attribute in child:
                if attribute.tag == 'backgroundimage':
                    bg_link = attribute.text
                elif attribute.tag == 'package':
                    package_name = attribute.text
                elif attribute.tag == 'pricing':
                    price = attribute.text
            if not package_name:
                raise Exception("Package name not specified for {}!".format(theme_name))
            if not bg_link:
                raise Exception("Link to background image not found for {}!".format(theme_name))
            is_free = verify_price(package_name)
            if type(is_free) != bool:
                raise Exception("Error retrieving Play Store data for {}".format(theme_name))
            if is_free and price != 'Free':
                raise Exception("{} is free on the Play Store but marked as paid in the database!".format(theme_name))
            elif not is_free and price != 'Paid':
                raise Exception("{} is paid on the Play Store but marked as free in the database!".format(theme_name))
            if requests.get(bg_link).status_code != 200:
                raise Exception("Invalid link specified for {}'s background image!".format(theme_name))
            print("Entry for {} is valid!".format(theme_name))


if __name__ == '__main__':
    main()
