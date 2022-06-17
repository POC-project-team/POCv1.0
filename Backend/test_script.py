import requests as r


def random_string():
    import string
    import random
    return ''.join(random.choice(string.ascii_letters) for i in range(10))


def main():
    for _ in range (100):
        login = random_string()
        password = login
        r.post(url='http://217.25.88.71:60494/signup', json={'login': login, 'password': password})
        token = r.post(url='http://217.25.88.71:60494/auth', json={'login': login, 'password': password})
        print(token.json()['token'])

        tag_name = random_string()

        create_link = f"http://217.25.88.71:60494/{token.json()['token']}/{tag_name}/tag"

        create = r.post(url=create_link, json={'tagName': "test"})
        print(create.json())

        add_note = f"http://217.25.88.71:60494/{token.json()['token']}/{tag_name}/note"
        r.post(url=add_note, json={'note': "test"})
        r.post(url=add_note, json={'note': "test123"})


if __name__ == '__main__':
    main()
