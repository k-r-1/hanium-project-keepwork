package com.example.a23_hf069

import JobPosting
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RetrofitInterface {
    companion object {
        const val API_URL = "http://13.125.126.74:8000/"
    }
    @GET("keepwork/member_personal/{personal_id}/")
    fun getData(@Path("personal_id") id: String): Call<P_MemberModel>


    @POST("keepwork/member_personal/")
    fun postData(@Body data: P_MemberModel): Call<P_MemberModel>

    @GET("keepwork/member_company/{company_id}/")
    fun getCorporateData(@Path("company_id") id: String): Call<C_MemberModel>

//    @GET("keepwork/member_company/")
//    fun getCorporateData(@Query("company_id") id: String): Call<List<C_MemberModel>> // 추가: C_MemberModel 사용

//    @GET("keepwork/member_company/")
//    fun getCorporateData(@Query("company_id") id: String, @Query("company_password") password: String): Call<List<C_MemberModel>> // 추가


    @POST("keepwork/member_company/")
    fun postCorporateData(@Body data: C_MemberModel): Call<C_MemberModel>

   /* @GET("keepwork/job/")
    fun getJobPostingData(@Query("company_name") id: String): Call<List<JobPosting>>*/

    @GET("keepwork/job/")
    fun getJobPostingData(@Query("userName") userName: String): Call<List<JobPosting>>

    @POST("keepwork/job/")
    fun postJob(@Body job: JobPosting): Call<JobPosting>

    @GET("keepwork/job/all/") // 수정
    fun getAllJobPostings(): Call<List<JobPosting>>

    @GET("keepwork/job/")
    fun getJobPostings(): Call<List<JobPosting>>

    @DELETE("keepwork/job/{listnum}/")
    fun deleteJobPosting(@Path("listnum") listnum: Int): Call<Void>

    @GET("keepwork/resume/")
    fun getResumeData(@Query("personal_id") personalId: String): Call<List<ResumeModel>>

    @PUT("keepwork/job/{listnum}/") // 수정
    fun updateJobPosting(@Path("listnum") listnum: Int, @Body jobPosting: JobPosting): Call<JobPosting>


    @GET("keepwork/notice/")
    fun getNoticeData(
        @Query("notice_listnum") noticeListNum: Int?,
        @Query("notice_title") noticeTitle: String?,
        @Query("notice_content") noticeContent: Int?,
        @Query("notice_date") noticeDate : String?
    ): Call<List<NoticeModel>>

    @GET("keepwork/resume/")
    fun getResumeData(
        @Query("personal_id") userId: String?,
        @Query("resume_listnum") resumeListnum: Int?,
        @Query("resume_title") resumeTitle: String?,
        @Query("resume_academic") resumeAcademic: String?,
        @Query("resume_career") resumeCareer: String?,
        @Query("resume_introduction") resumeIntroduction: String?,
        @Query("resume_certificate") resumeCertificate: String?,
        @Query("resume_learning") resumeLearning: String?,
        @Query("resume_desire") resumeDesire: String?,
        @Query("resume_complete") resumeComplete: String?,
    ): Call<List<ResumeModel>>

    @POST("keepwork/resume/")
    fun deleteResumeFromServer(
        @Field("personal_id") userId: String,
        @Field("resume_listnum") resumeListNum: Int
    ): Call<String>
}