using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using Mirror;

public class DrawCards : NetworkBehaviour
{
    public PlayerManager PlayerManager;

    public void OnCLick()
    {
        NetworkIdentity NetworkIdentity = NetworkClient.connection.identity;
        PlayerManager = NetworkIdentity.GetComponent<PlayerManager>();
        PlayerManager.CmdDealCards();
    }
}
