package com.aikundli.util;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.aikundli.model.SavedReport;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SavedReportDao_Impl implements SavedReportDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SavedReport> __insertionAdapterOfSavedReport;

  private final EntityDeletionOrUpdateAdapter<SavedReport> __deletionAdapterOfSavedReport;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public SavedReportDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSavedReport = new EntityInsertionAdapter<SavedReport>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `saved_reports` (`id`,`name`,`dateOfBirth`,`ascendant`,`moonSign`,`planetsJson`,`horoscopeJson`,`pdfPath`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SavedReport entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getDateOfBirth());
        statement.bindString(4, entity.getAscendant());
        statement.bindString(5, entity.getMoonSign());
        statement.bindString(6, entity.getPlanetsJson());
        statement.bindString(7, entity.getHoroscopeJson());
        if (entity.getPdfPath() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getPdfPath());
        }
        statement.bindLong(9, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfSavedReport = new EntityDeletionOrUpdateAdapter<SavedReport>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `saved_reports` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SavedReport entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM saved_reports";
        return _query;
      }
    };
  }

  @Override
  public Object insertReport(final SavedReport report,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSavedReport.insert(report);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteReport(final SavedReport report,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfSavedReport.handle(report);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SavedReport>> getAllReports() {
    final String _sql = "SELECT * FROM saved_reports ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"saved_reports"}, new Callable<List<SavedReport>>() {
      @Override
      @NonNull
      public List<SavedReport> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDateOfBirth = CursorUtil.getColumnIndexOrThrow(_cursor, "dateOfBirth");
          final int _cursorIndexOfAscendant = CursorUtil.getColumnIndexOrThrow(_cursor, "ascendant");
          final int _cursorIndexOfMoonSign = CursorUtil.getColumnIndexOrThrow(_cursor, "moonSign");
          final int _cursorIndexOfPlanetsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "planetsJson");
          final int _cursorIndexOfHoroscopeJson = CursorUtil.getColumnIndexOrThrow(_cursor, "horoscopeJson");
          final int _cursorIndexOfPdfPath = CursorUtil.getColumnIndexOrThrow(_cursor, "pdfPath");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<SavedReport> _result = new ArrayList<SavedReport>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SavedReport _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDateOfBirth;
            _tmpDateOfBirth = _cursor.getString(_cursorIndexOfDateOfBirth);
            final String _tmpAscendant;
            _tmpAscendant = _cursor.getString(_cursorIndexOfAscendant);
            final String _tmpMoonSign;
            _tmpMoonSign = _cursor.getString(_cursorIndexOfMoonSign);
            final String _tmpPlanetsJson;
            _tmpPlanetsJson = _cursor.getString(_cursorIndexOfPlanetsJson);
            final String _tmpHoroscopeJson;
            _tmpHoroscopeJson = _cursor.getString(_cursorIndexOfHoroscopeJson);
            final String _tmpPdfPath;
            if (_cursor.isNull(_cursorIndexOfPdfPath)) {
              _tmpPdfPath = null;
            } else {
              _tmpPdfPath = _cursor.getString(_cursorIndexOfPdfPath);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new SavedReport(_tmpId,_tmpName,_tmpDateOfBirth,_tmpAscendant,_tmpMoonSign,_tmpPlanetsJson,_tmpHoroscopeJson,_tmpPdfPath,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
