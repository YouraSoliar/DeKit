<<<<<<<< HEAD:app/src/main/java/com/example/mlbirds/data/room/dao/BirdsDao.java
package com.example.mlbirds.data.room.dao;
========
package com.example.dekit;
>>>>>>>> master:app/src/main/java/com/example/dekit/BirdsDao.java

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.dekit.room.enteties.Bird;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface BirdsDao {

    @Query("SELECT * FROM birds")
    Single<List<Bird>> getBirds();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable add (Bird bird);

    @Query("DELETE FROM birds WHERE id=:id")
    Completable remove(int id);
}
