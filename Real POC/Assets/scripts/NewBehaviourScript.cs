using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
public class NewBehaviourScript : MonoBehaviour
{
    public Text hpText;

    [SerializeField] int hp = 10000;
    public void buttonclick(){
        hp-=10;
        if (hp<=0){
            Camera.main.GetComponent<winloose>().Win();
        }
    }

    // Update is called once per frame
    void Update()
    {
        hpText.text = hp.ToString();
    }
}
