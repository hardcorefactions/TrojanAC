package net.valorhcf.trojan.log;

import cc.fyre.core.api.response.EmptyApiResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;

public interface LogService {

    @PUT(".")
    Call<EmptyApiResponse> create(@Body LogBody body);

}
