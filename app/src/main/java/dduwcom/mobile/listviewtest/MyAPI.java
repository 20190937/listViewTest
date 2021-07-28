package dduwcom.mobile.test;

import java.util.List;

import dduwcom.mobile.listviewtest.PostItem;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/*
Call 을 통해서 웹서버에 요청을 보낼 수 있다.

1. Http요청을 어노테이션으로 명시

2. URL Parameter와 Query Parameter 사용이 가능하다.
 ex) @GET("/group/{id}/users") Call<List<User>> groupList(@Path("id") int groupId, @Query("sort") String sort)

3. 객체는 Body 로 json형태로 전달한다.
url 끝에 / 를 빼먹으면 error 가 발생할 수 있으니 유의바란다.

 */

public interface MyAPI {
    @POST("/posts/")
    Call<PostItem> post_posts(@Body PostItem post);

    @PATCH("/posts/{pk}/")
    Call<PostItem> patch_posts(@Path("pk") int pk, @Body PostItem post);

    @DELETE("/posts/{pk}/")
    Call<PostItem> delete_posts(@Path("pk") int pk);

    @GET("/posts/")
    Call<List<PostItem>> get_posts();

    @GET("/posts/{pk}/")
    Call<PostItem> get_post_pk(@Path("pk") int pk);
}
