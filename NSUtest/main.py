import requests
import re
import json
from Student import Student


def main():
    # name = input("Tell me you name:\n")
    # group = int(input("Tell me your group number:\n"))
    name = "Sasha"
    group = 20214

    try:
        student = Student(name, group)
        student.login = "a.sartakov1"
        student.password = "qH!6$f5J"

        if requests.get(f"https://table.nsu.ru/group/{group}").status_code != 200:
            print("No such group")
            raise ValueError
    except ValueError as e:
        print(e)
        exit(1)
    else:
        while (answer := input("What do you wanna do?\n")) != "exit":
            if answer.lower() == "schedule":
                student.get_schedule()

            elif answer.lower() == "exam":

                student.get_exams()
            elif answer.lower() == "notebook":

                if not hasattr(student, "login"):
                    student.login = input("Put your login:\n")
                if not hasattr(student, "password"):
                    student.password = input("Put your password:\n")

                student.get_notebook()
            else:
                print("I don't know what do you want from me")


if __name__ == '__main__':
    main()
