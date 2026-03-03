import yaml
import time
from pypresence import Presence, ActivityType, StatusDisplayType
from bs4 import BeautifulSoup
from urllib.parse import urlparse
import requests

def get_tabs():
    tab_response = requests.get(
        url='http://localhost:6000/json/list'
    ).json()
    return tab_response

def find_pornhub_tab():
    tabs = get_tabs()
    for tab in tabs:
        parsed = urlparse(tab['url'])
        if parsed.netloc == "www.pornhub.com" and parsed.path == "/view_video.php":
            return tab['url']
        else:
            continue
    return None

def get_video_info():
    url = find_pornhub_tab()
    if url is None:
        return None
    else:
        pornhub_response = requests.get(
            url=url
        )
        return pornhub_response.text

def parse_video_info():
    video_info = get_video_info()
    if video_info is None:
        return None
    else:
        soup = BeautifulSoup(video_info, "html.parser")
        og_tags = soup.find_all("meta", property=lambda x: x and x.startswith("og:"))
        og_data = {}
        for tag in og_tags:
            property_name = tag.get("property")
            content = tag.get("content")
            if property_name and content:
                og_data[property_name] = content
        return og_data

def get_url_and_description():
    info = parse_video_info()
    if info is None:
        return None
    else:
        return {
            'title': info["og:title"],
            'url': info["og:url"]
        }

with open("token.yaml", "r") as file:
    data = yaml.safe_load(file)

RPC = Presence(data['discord'])
RPC.connect()

while True:
    video_info = get_url_and_description()
    if video_info is None:
        RPC.clear()
    else:
        RPC.update(
            state="Gooning",
            details=f"{video_info['title']}",
            large_image="phlogo",
            large_text="PornHub",
            buttons=[
                {"label": "Watch", "url": f"{video_info['url']}"}
            ],
            activity_type=ActivityType.WATCHING,
            status_display_type=StatusDisplayType.STATE,
            name=f"{video_info['title']}"
        )
    time.sleep(15)
