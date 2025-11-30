//package com.example.mobileapp.ui.activity;
//
//import android.app.DatePickerDialog;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.mobileapp.R;
//import com.example.mobileapp.data.model.Event;
//import com.example.mobileapp.network.ApiService;
//import com.example.mobileapp.network.EventSearchResponse;
//import com.example.mobileapp.network.FilterResponse;
//import com.example.mobileapp.network.RetrofitClient;
//import com.example.mobileapp.network.ViewAllEventResponse;
//import com.example.mobileapp.ui.adapter.EventAdapter;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//import java.util.Locale;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class SearchActivity extends AppCompatActivity {
//
//    EditText edtKeyword;
//    TextView tvSelectDate;
//    RecyclerView rvSearchResults;
//
//    ApiService apiService;
//    EventAdapter adapter;
//    List<Event> eventList = new ArrayList<>();
//
//    Calendar selectedDate;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search);
//
//        apiService = RetrofitClient.getInstance().create(ApiService.class);
//
//        edtKeyword = findViewById(R.id.edtKeyword);
//        tvSelectDate = findViewById(R.id.tvSelectDate);
//        rvSearchResults = findViewById(R.id.rvSearchResults);
//
//        // RecyclerView 2 cột
//        rvSearchResults.setLayoutManager(new GridLayoutManager(this, 2));
//        adapter = new EventAdapter(this, eventList);
//        rvSearchResults.setAdapter(adapter);
//
//        // Khi nhập từ khóa
//        edtKeyword.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                searchEvents();
//            }
//            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            @Override public void afterTextChanged(Editable s) {}
//        });
//
//        // Chọn ngày
//        tvSelectDate.setOnClickListener(v -> openDatePicker());
//    }
//
//    private void openDatePicker() {
//        Calendar now = Calendar.getInstance();
//
//        DatePickerDialog dialog = new DatePickerDialog(
//                this,
//                (view, year, month, day) -> {
//                    selectedDate = Calendar.getInstance();
//                    selectedDate.set(year, month, day);
//
//                    String dateText = year + "-" + (month + 1) + "-" + day;
//                    tvSelectDate.setText(dateText);
//
//                    searchEvents();
//                },
//                now.get(Calendar.YEAR),
//                now.get(Calendar.MONTH),
//                now.get(Calendar.DAY_OF_MONTH)
//        );
//
//        dialog.show();
//    }
//
//    private void searchEvents() {
//        String keyword = edtKeyword.getText().toString().trim();
//        String date = null;
//
//        if (selectedDate != null) {
//            date = String.format(Locale.getDefault(),
//                    "%04d-%02d-%02d",
//                    selectedDate.get(Calendar.YEAR),
//                    selectedDate.get(Calendar.MONTH) + 1,
//                    selectedDate.get(Calendar.DAY_OF_MONTH)
//            );
//        }
//
//        // Gọi API tìm kiếm
//        apiService.searchEvent(keyword).enqueue(new Callback<EventSearchResponse>() {
//            public void onResponse(
//                    Call<EventSearchResponse> call,
//                    Response<EventSearchResponse> response
//            ) {
//                if (response.isSuccessful() && response.body() != null) {
//                    List<Event> events = response.body().getEvents();
//                    if (events == null) events = new ArrayList<>();
////                    adapter = new EventAdapter(SearchActivity.this, events);
////                    rvSearchResults.setAdapter(adapter);
//                    adapter.updateData(events);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<EventSearchResponse> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });
//    }
//    private void filterByDate(String date) {
//        apiService.filterEventsByDate(date).enqueue(new Callback<FilterResponse>() {
//            @Override
//            public void onResponse(Call<FilterResponse> call, Response<FilterResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    List<Event> events = response.body().getData();
//                    if (events == null) events = new ArrayList<>();
//                    adapter.updateData(events);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<FilterResponse> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });
//}
//
 /// //////////////////////////////////////////////////////////////////////
package com.example.mobileapp.ui.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;
import com.example.mobileapp.data.model.Event;
import com.example.mobileapp.network.ApiService;
import com.example.mobileapp.network.EventSearchResponse;
import com.example.mobileapp.network.RetrofitClient;
import com.example.mobileapp.ui.adapter.EventAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//public class SearchActivity extends AppCompatActivity {
//
//    EditText edtKeyword;
//    TextView tvSelectDate;
//    Spinner spCategory;
//    RecyclerView rvSearchResults;
//
//    ApiService apiService;
//    EventAdapter adapter;
//    List<Event> eventList = new ArrayList<>();
//    List<String> categoryList = new ArrayList<>();
//    Calendar selectedDate;
//    Integer selectedCategoryId = null; // mặc định không chọn category
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search);
//
//        apiService = RetrofitClient.getInstance().create(ApiService.class);
//
//        edtKeyword = findViewById(R.id.edtKeyword);
//        tvSelectDate = findViewById(R.id.tvSelectDate);
//        spCategory = findViewById(R.id.spCategory);
//        rvSearchResults = findViewById(R.id.rvSearchResults);
//
//        // RecyclerView 2 cột
//        rvSearchResults.setLayoutManager(new GridLayoutManager(this, 2));
//        adapter = new EventAdapter(this, eventList);
//        rvSearchResults.setAdapter(adapter);
//
//        // Load category vào spinner
//        loadCategories();
//
//        // Khi nhập từ khóa
//        edtKeyword.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                searchEvents();
//            }
//            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            @Override public void afterTextChanged(Editable s) {}
//        });
//
//        // Chọn ngày
//        tvSelectDate.setOnClickListener(v -> openDatePicker());
//
//        // Chọn category
//        spCategory.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
//                // Lưu category_id tương ứng
//                selectedCategoryId = position >= 0 ? position + 1 : -1; // giả sử category_id bắt đầu từ 1
//                searchEvents();
//            }
//
//            @Override
//            public void onNothingSelected(android.widget.AdapterView<?> parent) {
//                selectedCategoryId = -1;
//                searchEvents();
//            }
//        });
//    }
//
//    private void loadCategories() {
//        // Ví dụ tĩnh, bạn có thể load từ API
//        categoryList.add("Tất cả");
//        categoryList.add("Music");
//        categoryList.add("Sports");
//        categoryList.add("Art");
//
//        ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(this,
//                android.R.layout.simple_spinner_item, categoryList);
//        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spCategory.setAdapter(adapterCategory);
//    }
//
//    private void openDatePicker() {
//        Calendar now = Calendar.getInstance();
//
//        DatePickerDialog dialog = new DatePickerDialog(
//                this,
//                (view, year, month, day) -> {
//                    selectedDate = Calendar.getInstance();
//                    selectedDate.set(year, month, day);
//
//                    String dateText = String.format("%04d-%02d-%02d", year, month + 1, day);
//                    tvSelectDate.setText(dateText);
//
//                    searchEvents();
//                },
//                now.get(Calendar.YEAR),
//                now.get(Calendar.MONTH),
//                now.get(Calendar.DAY_OF_MONTH)
//        );
//
//        dialog.show();
//    }
//
//    private void searchEvents() {
//        String keyword = edtKeyword.getText().toString().trim();
//        String date = null;
//
//        if (selectedDate != null) {
//            date = String.format(Locale.getDefault(),
//                    "%04d-%02d-%02d",
//                    selectedDate.get(Calendar.YEAR),
//                    selectedDate.get(Calendar.MONTH) + 1,
//                    selectedDate.get(Calendar.DAY_OF_MONTH)
//            );
//        }
//
//        Integer categoryId = selectedCategoryId > 0 ? selectedCategoryId : null;
//
//        // Gọi API search + filter
//        apiService.searchEvent(keyword, date, categoryId).enqueue(new Callback<EventSearchResponse>() {
//            @Override
//            public void onResponse(Call<EventSearchResponse> call, Response<EventSearchResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    List<Event> events = response.body().getEvents();
//                    if (events == null) events = new ArrayList<>();
//                    adapter.updateData(events);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<EventSearchResponse> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });
//    }
//}
 public class SearchActivity extends AppCompatActivity {

     EditText edtKeyword;
     TextView tvSelectDate;
     Spinner spCategory; // --- CHỖ CẦN CHÚ Ý: Thêm Spinner để chọn category ---
     RecyclerView rvSearchResults;

     ApiService apiService;
     EventAdapter adapter;
     List<Event> eventList = new ArrayList<>();
     List<String> categoryList = new ArrayList<>();
     List<Integer> categoryIdList = new ArrayList<>(); // --- CHỖ CẦN CHÚ Ý: map chính xác category_id từ DB ---
     Calendar selectedDate;
     Integer selectedCategoryId = null; // --- CHỖ CẦN CHÚ Ý: null nếu không chọn category ---

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_search);
         Toolbar toolbar = findViewById(R.id.toolbarSearch);
         setSupportActionBar(toolbar);
         toolbar.setNavigationOnClickListener(v -> finish()); // quay về MainActivity

         apiService = RetrofitClient.getInstance().create(ApiService.class);

         edtKeyword = findViewById(R.id.edtKeyword);
         tvSelectDate = findViewById(R.id.tvSelectDate);
         spCategory = findViewById(R.id.spCategory);
         rvSearchResults = findViewById(R.id.rvSearchResults);

         rvSearchResults.setLayoutManager(new GridLayoutManager(this, 2));
         adapter = new EventAdapter(this, eventList);
         rvSearchResults.setAdapter(adapter);

         // Load category vào spinner
         loadCategories();

         // Khi nhập từ khóa
         edtKeyword.addTextChangedListener(new TextWatcher() {
             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {
                 searchEvents();
             }
             @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
             @Override public void afterTextChanged(Editable s) {}
         });

         // Chọn ngày
         tvSelectDate.setOnClickListener(v -> openDatePicker());

         // Chọn category
         spCategory.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                 // --- CHỖ CẦN CHÚ Ý: lấy category_id thực từ danh sách map ---
                 selectedCategoryId = categoryIdList.get(position); // null nếu "Tất cả"
                 searchEvents();
             }

             @Override
             public void onNothingSelected(android.widget.AdapterView<?> parent) {
                 selectedCategoryId = null;
                 searchEvents();
             }
         });
     }

     private void loadCategories() {
         // Ví dụ tĩnh, bạn có thể load từ API
         categoryList.add("Tất cả");
         categoryList.add("Music");
         categoryList.add("Sports");
         categoryList.add("Art");

         // --- CHỖ CẦN CHÚ Ý: map category_id thực từ DB ---
         categoryIdList.add(null); // Tất cả
         categoryIdList.add(1);    // Music
         categoryIdList.add(2);    // Sports
         categoryIdList.add(3);    // Art

         ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(this,
                 android.R.layout.simple_spinner_item, categoryList);
         adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         spCategory.setAdapter(adapterCategory);
     }

     private void openDatePicker() {
         Calendar now = Calendar.getInstance();

         DatePickerDialog dialog = new DatePickerDialog(
                 this,
                 (view, year, month, day) -> {
                     selectedDate = Calendar.getInstance();
                     selectedDate.set(year, month, day);

                     String dateText = String.format("%04d-%02d-%02d", year, month + 1, day);
                     tvSelectDate.setText(dateText);

                     searchEvents();
                 },
                 now.get(Calendar.YEAR),
                 now.get(Calendar.MONTH),
                 now.get(Calendar.DAY_OF_MONTH)
         );

         dialog.show();
     }

     private void searchEvents() {
         String keyword = edtKeyword.getText().toString().trim();
         String date = null;

         if (selectedDate != null) {
             date = String.format(Locale.getDefault(),
                     "%04d-%02d-%02d",
                     selectedDate.get(Calendar.YEAR),
                     selectedDate.get(Calendar.MONTH) + 1,
                     selectedDate.get(Calendar.DAY_OF_MONTH)
             );
         }

         // --- CHỖ CẦN CHÚ Ý: selectedCategoryId có thể null nếu "Tất cả" ---
         apiService.searchEvent(keyword, date, selectedCategoryId)
                 .enqueue(new Callback<EventSearchResponse>() {
                     @Override
                     public void onResponse(Call<EventSearchResponse> call, Response<EventSearchResponse> response) {
                         if (response.isSuccessful() && response.body() != null) {
                             List<Event> events = response.body().getEvents();
                             if (events == null) events = new ArrayList<>();
                             adapter.updateData(events); // --- CHỖ CẦN CHÚ Ý: cập nhật adapter ---
                         }
                     }

                     @Override
                     public void onFailure(Call<EventSearchResponse> call, Throwable t) {
                         t.printStackTrace();
                     }
                 });
     }
 }

