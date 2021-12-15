import json
import requests


class Student:
    def __init__(self, name, group, login=None, password=None):
        self.schedule = None
        self.exams = None
        self.name = name
        self.group = group
        self.login = None
        self.password = None

    '''
    Make a request to get the schedule from site
    '''
    def get_schedule(self):
        html = requests.get(f"https://table.nsu.ru/group/{self.group}")
        self.schedule = html.text
        print("Schedule")

        # It has strange encoding
        with open("Schedule.txt", "w", encoding='utf8') as file:
            file.write(html.text)

        # Wanna store it as dictionary to make a list of lessons
        with open("Schedule.txt", "r", encoding='utf8') as file:
            self.schedule = json.loads(file.read())

        print(type(self.schedule))

        # Wanted to parse it with
        '''    
        # Todo it 7 times, there's gonna be a way for this, but it's a draft
        with open("Schedule.txt", "r", encoding='utf8') as file:
            self.schedule = file
            
        # Finding within the information 
        start_first = self.schedule.index("<td>9:00</td>")
        end_first = self.schedule.index("<td>10:50</td>")

        print(f"The first pair information is \n{self.schedule[start_first:end_first]}")

        #print(re.findall(self.schedule[start_first:end_first], '<div class="cell">'))
        '''
        '''
        print(f"The second pair information is\n"
              f"{self.schedule[self.schedule.index('<td>10:50</td>'):self.schedule.index('<td>12:40</td>')]}")
        '''

    def get_exams(self):
        html = requests.get(f"https://table.nsu.ru/exam/{self.group}")
        self.exams = html.text
        with open("Exams.txt", "w", encoding='utf8') as file:
            file.write(html.text)
        print("Exams")

    def get_notebook(self):
        payload = {
            "login": self.login,
            "password": self.password
        }
        with requests.Session() as s:
            #TODO how to acces that thing?
            p = s.get("https://cab.nsu.ru/user/sign-in/auth?authclient=nsu")
            p = s.post("link", data=payload)
            print(p.status_code)

            html = requests.get(f"https://cab.nsu.ru/student/grade")
            print(html.text)
