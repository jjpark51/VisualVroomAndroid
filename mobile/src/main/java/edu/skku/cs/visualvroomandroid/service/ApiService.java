package edu.skku.cs.visualvroomandroid.service;


import edu.skku.cs.visualvroomandroid.dto.AudioData;
import edu.skku.cs.visualvroomandroid.dto.PredictionResponse;
import retrofit2.http.POST;
import retrofit2.Call;
import retrofit2.http.Body;

public interface ApiService {

    @POST("analyze")
    Call<PredictionResponse> analyzeAudio(AudioData audioData);
}
