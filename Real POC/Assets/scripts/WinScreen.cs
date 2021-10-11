using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;

public class WinScreen : MonoBehaviour
{
    [SerializeField] int clicks;
    public Text clickstxt;

    public void Reset(int totalclicks)
    {
        clicks = totalclicks;
        gameObject.SetActive(true);
    }

    public void RestartButton() {
        gameObject.SetActive(false);
    }

    void Update()
    {
        clickstxt.text = "TOTAL CLICKS: " + clicks.ToString();
    }
}
