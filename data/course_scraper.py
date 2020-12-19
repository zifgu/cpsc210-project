import json
import sys
import requests
from bs4 import BeautifulSoup

def get_course(dept, number, required):
    URL = 'https://courses.students.ubc.ca/cs/courseschedule?pname=subjarea&tname=subj-course&dept=' + dept.upper() + '&course=' + number
    page = requests.get(URL)
    soup = BeautifulSoup(page.content, 'html.parser')

    course = {}
    course["name"] = dept.upper() + number
    course["required"] = required

    results = soup.find("table", class_="table table-striped section-summary")

    sections = results.find_all("tr")
    sections_list = []

    for section in sections:
        status = section.contents[0].string
        activity = section.contents[2].string

        if (activity == "Web-Oriented Course" or activity == "Lecture"):
            section_dict = {}
            
            section_name = section.contents[1].string.split()[2]
            section_dict["name"] = section_name
            
            term = section.contents[3].string
            days = section.contents[5].string
            start = section.contents[6].string
            end = section.contents[7].string
            section_dict["times"] = list_times(days, term, start, end)

            sections_list.append(section_dict)
        
        # if (status != "Full" and status != "Restricted" and status != "Blocked"):
        # TODO: also filter out STT entries, which do not have time information
    
    course["sections"] = sections_list

    with open('./data/temp_course.json', 'w') as f:
        json.dump(course, f, indent = 4)

def list_times(days, term, start_time, end_time):
    days_of_week = days.split()
    times = []
    switch = {
        "Mon": "MONDAY",
        "Tue": "TUESDAY",
        "Wed": "WEDNESDAY",
        "Thu": "THURSDAY",
        "Fri": "FRIDAY",
        "Sat": "SATURDAY",
        "Sun": "SUNDAY"
    }
    for day in days_of_week:
        time = {}
        time["start"] = format_time(start_time)
        time["end"] = format_time(end_time)
        time["term"] = int(term)
        time["day"] = switch.get(day)
        times.append(time)
    return times

def format_time(time_string):
    if len(time_string) == 4:
        return "0" + time_string
    else:
        return time_string

if __name__ == "__main__":
    get_course(sys.argv[1], sys.argv[2], False)