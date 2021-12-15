import requests
from decouple import config
from Student import Student


def main():
    # name = input("Tell me you name:\n")
    # group = int(input("Tell me your group number:\n"))
    name = config("NAME")
    group = config("GROUP")
    # Whether the group exists
    try:
        student = Student(name, group)
        student.get_schedule()
    except KeyError:
        print("The group wasn't found")
        exit(1)
    else:
        while (answer := input("What do you wanna do?\n")) != "exit":
            if answer.lower() == "schedule":
                student.get_schedule()

            elif answer.lower() == "exam":

                student.get_exams()
            elif answer.lower() == "notebook":

                try:
                    student.login = config("LOGIN")
                    student.password = config("PASSWORD")
                # If there's no login or key in .env
                except KeyError:
                    if not hasattr(student, "login"):
                        student.login = input("Put your login:\n")
                    if not hasattr(student, "password"):
                        student.password = input("Put your password:\n")

                finally:
                    student.get_notebook()

            else:
                print("I don't know what do you want from me")


if __name__ == '__main__':
    main()
