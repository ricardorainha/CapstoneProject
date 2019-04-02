package com.ricardorainha.mustache.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.ricardorainha.mustache.R;
import com.ricardorainha.mustache.databinding.BarbershopsListItemBinding;
import com.ricardorainha.mustache.model.Barbershop;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class BarbershopsAdapter extends RecyclerView.Adapter<BarbershopsAdapter.BarbershopsViewHolder> {

    private BarbershopsListItemBinding binding;
    private List<Barbershop> barbershops;

    public BarbershopsAdapter(List<Barbershop> barbershops) {
        this.barbershops = barbershops;
    }

    @NonNull
    @Override
    public BarbershopsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = DataBindingUtil.inflate(inflater, R.layout.barbershops_list_item, parent, false);
        BarbershopsViewHolder viewHolder = new BarbershopsViewHolder(binding);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BarbershopsViewHolder holder, int position) {
        holder.bind(barbershops.get(position));
    }

    @Override
    public int getItemCount() {
        if (barbershops != null) {
            return barbershops.size();
        }

        return 0;
    }

    class BarbershopsViewHolder extends RecyclerView.ViewHolder {
        BarbershopsListItemBinding binding;

        public BarbershopsViewHolder(@NonNull BarbershopsListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Barbershop barbershop) {
            if (barbershop.getPhotos() != null && barbershop.getPhotos().size() > 0) {
                Glide.with(binding.getRoot().getContext()).load(barbershop.getPhotoUrl()).into(binding.ivPhoto);
            }
            binding.tvName.setText(barbershop.getName());
            binding.tvRating.setText(String.valueOf(barbershop.getRating()));
            binding.tvAddress.setText(barbershop.getFormattedAddress());
            binding.executePendingBindings();
        }

    }
}
