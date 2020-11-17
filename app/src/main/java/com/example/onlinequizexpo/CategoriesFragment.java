package com.example.onlinequizexpo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.onlinequizexpo.Interface.ItemClickListener;
import com.example.onlinequizexpo.Model.Category;
import com.example.onlinequizexpo.ViewHolder.CategoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends Fragment {

    View myFragment;
    RecyclerView listCategory;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference categories;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    public static androidx.fragment.app.Fragment newInstance(){
        CategoriesFragment categoriesFragment = new CategoriesFragment();
        return categoriesFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        categories = database.getReference("Category");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragment = inflater.inflate(R.layout.fragment_categories, container, false);

        listCategory = myFragment.findViewById(R.id.list_category);
        listCategory.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(container.getContext()); // sets a recyclerview on a fragment
        listCategory.setLayoutManager(layoutManager);

        loadCategories();
        return myFragment;
    }

    private void loadCategories() {
        adapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(
                Category.class,
                R.layout.category_layout,
                CategoryViewHolder.class,
                categories
        ) {
            @Override
            protected void populateViewHolder(final CategoryViewHolder categoryViewHolder, final Category category, int i) {
                categoryViewHolder.categoryName.setText(category.getName());
                Picasso.get()
                        .load(category.getImage())
                        .into(categoryViewHolder.categoryImage);

                categoryViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLong) {
//                        Toast.makeText(getActivity(),String.format("%s|%s", adapter.getRef(position).getKey(),category.getName()) , Toast.LENGTH_SHORT).show();
                        if (adapter.getRef(position).getKey().equals("02")){
                            Log.d("EQUA", "onClick: Tic Tac Toe Clicked!");
//                            Toast.makeText(getActivity(), "Tic Tac Toe Selected!!", Toast.LENGTH_SHORT).show();
                            Intent ticIntent = new Intent(getActivity(), TicTacToe.class);
                            startActivity(ticIntent);
                        }else if (adapter.getRef(position).getKey().equals("04")){
                            Log.d("EQUA", "onClick: Puzzle Clicked!");
                            Toast.makeText(getActivity(), "Puzzle Selected!!", Toast.LENGTH_SHORT).show();
                        }else {
                            Intent startQuiz = new Intent(getActivity(),Start.class);
                            Common.categoryId = adapter.getRef(position).getKey();
                            Common.categoryName = category.getName();
                            startActivity(startQuiz);
                        }

                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        listCategory.setAdapter(adapter);
    }
}
