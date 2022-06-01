import requests as r
from time import sleep
import string
import asyncio
import random


ip = "http://0.0.0.0:60494"


def generate_string(size=6, chars=string.ascii_uppercase + string.digits):
    return ''.join(random.choice(chars) for _ in range(size))


def test():
    global ip
    test_string = generate_string()
    print("Test string: " + test_string)
    # registration
    r.post(f"{ip}/signup", data={"login": test_string, "password": test_string})
    # getting the token
    token = r.post(f"{ip}/auth", data={"login": test_string, "password": test_string})
    print (token.text)
    token = token.json()["token"]

    # adding a new tag
    r.post(f"{ip}/{token}/{test_string}/tag", data={"tagName": test_string})

    # adding note to the tag
    r.post(f"{ip}/{token}/{test_string}/note", data={"note": test_string})
    r.post(f"{ip}/{token}/{test_string}/note", data={"note": test_string})
    r.post(f"{ip}/{token}/{test_string}/note", data={"note": test_string})

    # deleting tag
    r.delete(f"{ip}/{token}/{test_string}/tag")

    # deleting user?
    sleep(10)



def main():
    # start test function
    test()


if __name__ == '__main__':
    main()
