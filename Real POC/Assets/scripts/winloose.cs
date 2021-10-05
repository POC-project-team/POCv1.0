using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class winloose : MonoBehaviour
{
    [SerializeField] private GameObject win; 
    // Update is called once per frame
    public void Win()
    {
        win.SetActive(true);
    }
}
