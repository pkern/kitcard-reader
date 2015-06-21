package de.Ox539.kitcard.reader;

import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;

public class MifareClassicHelper {

	/**
	 * MifareClassicHelper: This class helps with handling the NFC Tag and repairs possible errors
	 *
	 * @author Simon Tenbeitel <simon.tenbeitel@gmail.com>
	 */
	
	/**
	 * Repairs the broken tag on HTC devices running Android 5.x
	 * 
	 * "It seems, the reason of this bug in TechExtras of NfcA is null. However, TechList contains MifareClassic." -bildin
	 * For more information please refer to https://github.com/ikarus23/MifareClassicTool/issues/52#issuecomment-103797115
	 * 
	 * Code source: https://github.com/ikarus23/MifareClassicTool/issues/52#issuecomment-104277445
	 * 
	 * @param oTag The broken tag
	 * @return The fixed tag
	 */
	public static Tag repairTag(Tag oTag) {
		if (oTag == null) 
            return null;

        String[] sTechList = oTag.getTechList();

        Parcel oParcel, nParcel;

        oParcel = Parcel.obtain();
        oTag.writeToParcel(oParcel, 0);
        oParcel.setDataPosition(0);

        int len = oParcel.readInt();
        byte[] id = null;
        if (len >= 0)
        {
            id = new byte[len];
            oParcel.readByteArray(id);
        }
        int[] oTechList = new int[oParcel.readInt()];
        oParcel.readIntArray(oTechList);
        Bundle[] oTechExtras = oParcel.createTypedArray(Bundle.CREATOR);
        int serviceHandle = oParcel.readInt();
        int isMock = oParcel.readInt();
        IBinder tagService;
        if (isMock == 0)
        {
            tagService = oParcel.readStrongBinder();
        }
        else
        {
            tagService = null;
        }
        oParcel.recycle();

        int nfca_idx=-1;
        int mc_idx=-1;

        for(int idx = 0; idx < sTechList.length; idx++)
        {
            if(sTechList[idx] == NfcA.class.getName())
            {
                nfca_idx = idx;
            }
            else if(sTechList[idx] == MifareClassic.class.getName())
            {
                mc_idx = idx;
            }
        }

        if(nfca_idx>=0&&mc_idx>=0&&oTechExtras[mc_idx]==null)
        {
            oTechExtras[mc_idx] = oTechExtras[nfca_idx];
        }
        else
        {
            return oTag;
        }

        nParcel = Parcel.obtain();
        nParcel.writeInt(id.length);
        nParcel.writeByteArray(id);
        nParcel.writeInt(oTechList.length);
        nParcel.writeIntArray(oTechList);
        nParcel.writeTypedArray(oTechExtras,0);
        nParcel.writeInt(serviceHandle);
        nParcel.writeInt(isMock);
        if(isMock==0)
        {
            nParcel.writeStrongBinder(tagService);
        }
        nParcel.setDataPosition(0);

        Tag nTag = Tag.CREATOR.createFromParcel(nParcel);

        nParcel.recycle();

        return nTag;
    }
	
}