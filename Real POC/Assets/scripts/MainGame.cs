using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;
public class MainGame : MonoBehaviour
{
    [SerializeField] int hp = 800;
    [SerializeField] int ticks = 0;
    [SerializeField] int dmg = 20;
    public int pubdmg = 20;
    public Text hptxt;
    public Text tickstxt;
    public Text dmgtxt;
    public WinScreen WinScreen;
    public Choosedamage choosedamage;
    public void ButtonClick() {

        hp -= dmg;
        ticks++;
        if (hp <= 0)
        {
            if ((ticks < 8)){
                CheaterScene();
            }
            EndGame(ticks);
        }

    }


    public void EndGame(int clicks)
    {
        WinScreen.Reset(clicks);
        dmg = 20;
        hp = 800;
        ticks = 0;
    }
    public void CheaterScene() {
        choosedamage.Activate();
    }

    public void ChooseDmg() {
        if (dmg == 20)
        {
            dmg = 100;
        }
        else {
            dmg = 20;
        }

    }

    void Update()
    {
        hptxt.text = "HP: " + hp.ToString();
        tickstxt.text = "CLICKS: " + ticks.ToString();
        dmgtxt.text = "DMG: " + dmg.ToString();
        if (dmg != 20 && dmg != 100)
        {
            CheaterScene();
        }
    }
}
