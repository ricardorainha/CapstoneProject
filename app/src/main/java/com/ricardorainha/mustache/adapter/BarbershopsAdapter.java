package com.ricardorainha.mustache.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.ricardorainha.mustache.R;
import com.ricardorainha.mustache.databinding.BarbershopsListItemBinding;
import com.ricardorainha.mustache.model.Barbershop;
import com.ricardorainha.mustache.utils.FavoritesUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class BarbershopsAdapter extends RecyclerView.Adapter<BarbershopsAdapter.BarbershopsViewHolder> {

    private BarbershopsListItemBinding binding;
    private List<Barbershop> barbershops;
    private ActionCallback callback;

    public BarbershopsAdapter(List<Barbershop> barbershops, ActionCallback callback) {
        this.barbershops = barbershops;
        this.callback = callback;
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
            binding.btnDetails.setOnClickListener(v -> callback.onDetailsClicked(barbershop));
            checkFavoritesButtonText(barbershop);
            binding.btnFavorites.setOnClickListener(v -> {
                boolean isFavorite = FavoritesUtils.isFavorite(barbershop);
                if (isFavorite) {
                    FavoritesUtils.removeFavorite(barbershop.getPlaceId());
                }
                else {
                    FavoritesUtils.addFavorite(barbershop);
                }
                changeFavoritesButtonText(!isFavorite);
            });
            binding.executePendingBindings();
        }

        private void checkFavoritesButtonText(Barbershop barbershop) {
            changeFavoritesButtonText(FavoritesUtils.isFavorite(barbershop));
        }

        private void changeFavoritesButtonText(boolean isFavorite) {
            if (isFavorite) {
                binding.btnFavorites.setText(binding.getRoot().getContext().getString(R.string.remove_from_favorites));
            }
            else {
                binding.btnFavorites.setText(binding.getRoot().getContext().getString(R.string.add_to_favorites));
            }
        }

    }

    public interface ActionCallback {
        void onDetailsClicked(Barbershop barbershop);
    }
}
