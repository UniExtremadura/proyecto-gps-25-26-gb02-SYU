package com.gb02.syumsvc.model.dao;

import com.gb02.syumsvc.model.dto.MerchFavDTO;

public interface MerchFavDAO {
    public MerchFavDTO[] obtainMerchFavByUser(int idUsuario);
    public MerchFavDTO[] obtainMerchFavByMerch(int idMerch);
    public MerchFavDTO[] obtainMerchFav();
    public MerchFavDTO obtainMerchFav(int idMerch, int idUsuario);
    public boolean insertMerchFav(MerchFavDTO merchFav);
    public boolean deleteMerchFav(int idMerch, int idUsuario);
}
