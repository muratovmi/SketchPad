package com.itschoolsamsung.sketchpad.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itschoolsamsung.sketchpad.R;
import com.itschoolsamsung.sketchpad.adapter.OperationRecyclerViewAdapter;
import com.itschoolsamsung.sketchpad.customview.DrawingCanvas;
import com.itschoolsamsung.sketchpad.util.Constants;
import com.itschoolsamsung.sketchpad.util.SavePhotoUtil;

import java.io.File;
import java.util.Objects;

// Активити, содержащая холст для рисования, набор инструментов в нижней части экрана и боковую панель с палитрой цветов.
public class DrawingBoard extends AppCompatActivity implements View.OnClickListener,
        OperationRecyclerViewAdapter.OperationViewHolder.OperationCommunicator,
        SeekBar.OnSeekBarChangeListener {
    private static DrawerLayout mColorPalletDrawer;
    private DrawingCanvas mPaintCanvas;
    // Плитки с различными цветами находятся в NavigationView.
    private LayoutInflater mInflater;
    private ImageView mCurrentColorSelection;
    private final Handler mHandler = new Handler();
    private AlertDialog.Builder mBuilder;
    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_board);
        init();
    }

    public void init() {
        mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mColorPalletDrawer = findViewById(R.id.drawer_layout);
        // RecyclerView, в котором находится набор инструментов.
        RecyclerView mOperationRecyclerView = findViewById(R.id.oper_recyclerview);
        OperationRecyclerViewAdapter adapter = new OperationRecyclerViewAdapter(DrawingBoard.this, DrawingBoard.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(DrawingBoard.this, LinearLayoutManager.HORIZONTAL, false);
        mOperationRecyclerView.setLayoutManager(layoutManager);
        mOperationRecyclerView.setAdapter(adapter);
        // Получение id на всю палитру цветов в NavigationView, чтобы определять касание и изменять цвет кисти.
        // NavigationView, в котором находится палитра цветов.
        findViewById(R.id.navigation_view);
        ImageView mBlackColor = findViewById(R.id.black_color);
        mBlackColor.setOnClickListener(this);
        ImageView mBlueColor = findViewById(R.id.blue_color);
        mBlueColor.setOnClickListener(this);
        ImageView mGreenColor = findViewById(R.id.green_color);
        mGreenColor.setOnClickListener(this);
        ImageView mOrangeColor = findViewById(R.id.orange_color);
        mOrangeColor.setOnClickListener(this);
        ImageView mPinkColor = findViewById(R.id.pink_color);
        mPinkColor.setOnClickListener(this);
        ImageView mSkyblueColor = findViewById(R.id.skyblue_color);
        mSkyblueColor.setOnClickListener(this);
        ImageView mRedColor = findViewById(R.id.red_color);
        mRedColor.setOnClickListener(this);
        ImageView mVioletColor = findViewById(R.id.violet_color);
        mVioletColor.setOnClickListener(this);
        ImageView mWhiteColor = findViewById(R.id.white_color);
        mWhiteColor.setOnClickListener(this);
        mCurrentColorSelection = findViewById(R.id.current_color_selection);
        // SeekBar для изменения ширины обводки кисти или ластика.
        SeekBar mBrushStrokeSeeKBar = findViewById(R.id.brush_thickness_seek_bar);
        mBrushStrokeSeeKBar.setOnSeekBarChangeListener(this);
        mPaintCanvas = findViewById(R.id.drawing_canvas);
    }

    // Открытие палитры цветов.
    @SuppressLint("RtlHardcoded")
    public static void openColorDrawer() {
        if (mColorPalletDrawer != null) {
            mColorPalletDrawer.openDrawer(Gravity.LEFT);
        }
    }

    // Закрытие палитры цветов.
    @SuppressLint("RtlHardcoded")
    public static void closeColorDrawer() {
        if (mColorPalletDrawer != null) {
            mColorPalletDrawer.closeDrawer(Gravity.LEFT);
        }
    }

    // Метод onClick, который вызывается при выборе цвета в палитре цветов.
    @Override
    public void onClick(View v) {
        // Если выбран инструмент "Ластик", пользователь не сможет изменить цвет.
        if (mPaintCanvas.getCurrentOperation() != Constants.OPERATION_ERASE) {
            ImageView colorView = (ImageView) v;
            mPaintCanvas.changeBrushColor(colorView.getTag().toString());
            ((GradientDrawable) mCurrentColorSelection.getBackground()).setColor(Color.parseColor(colorView.getTag().toString()));
            if (mPaintCanvas.getCurrentOperation() == Constants.OPERATION_FILL_VIEW)
                mPaintCanvas.applyColorToView();
        } else {
            Toast.makeText(DrawingBoard.this, "Operation not allowed with current selection!", Toast.LENGTH_SHORT).show();
        }
        // Закрытие палитры цветов после выбора цвета.
        closeColorDrawer();
    }

    // Переопределённый метод getPosition, который вызывается при нажатии на один из инструментов в наборе инструментов.
    @Override
    public void getPosition(int position) {
        switch (position) {
            case Constants.OPERATION_DRAW_PENCIL:
                mPaintCanvas.setCurrentOperation(Constants.OPERATION_DRAW_PENCIL);
                mPaintCanvas.changeFillStyle(Constants.PAINT_STYLE_STROKE);
                break;
            case Constants.OPERATION_ERASE:
                mPaintCanvas.setCurrentOperation(Constants.OPERATION_ERASE);
                break;
            case Constants.OPERATION_CLEAR_CANVAS:
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(DrawingBoard.this);
                mBuilder.setTitle("New Drawing?");
                mBuilder.setMessage("Start new drawing (you will lose the current drawing)?");
                mBuilder.setPositiveButton("Yes", (dialog, which) -> {
                    mPaintCanvas.clearCompleteCanvas();
                    dialog.cancel();
                });
                mBuilder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
                mDialog.setCanceledOnTouchOutside(false);
                break;
            case Constants.OPERATION_UNDO:
                mPaintCanvas.undoPreviousOperation();
                break;
            case Constants.OPERATION_CHOOSE_COLOR:
                openColorDrawer();
                break;
            case Constants.OPERATION_DRAW_CIRCLE:
                mPaintCanvas.setCurrentOperation(Constants.OPERATION_DRAW_CIRCLE);
                mPaintCanvas.changeFillStyle(Constants.PAINT_STYLE_FILL);
                drawCircleOnBoard();
                break;
            case Constants.OPERATION_DRAW_RECTANGLE:
                mPaintCanvas.setCurrentOperation(Constants.OPERATION_DRAW_RECTANGLE);
                mPaintCanvas.changeFillStyle(Constants.PAINT_STYLE_FILL);
                drawRectangleOnBoard();
                break;
            case Constants.OPERATION_DRAW_OVAL:
                mPaintCanvas.setCurrentOperation(Constants.OPERATION_DRAW_OVAL);
                mPaintCanvas.changeFillStyle(Constants.PAINT_STYLE_FILL);
                drawCircleOnBoard(); // Changed method name
                break;
            case Constants.OPERATION_INSERT_TEXT:
                mPaintCanvas.setCurrentOperation(Constants.OPERATION_INSERT_TEXT);
                getTextInputFromUser();
                break;
            case Constants.OPERATION_SAVE_IMAGE:
                initiateSaveOperation();
                break;
            case Constants.OPERATION_SET_BACKGROUND:
                loadImageFromGallery();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    // Переопределённый метод onStopTrackingTouch, который вызывается для изменения ширины обводки кисти или ластика.
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Toast.makeText(this, " current stroke : " + seekBar.getProgress(), Toast.LENGTH_SHORT).show();
        mPaintCanvas.changeBrushStroke(seekBar.getProgress());
    }

    // Метод initiateSaveOperation, который сохраняет холст в галерее, перед этим запрашивая название будущего файла.
    public void initiateSaveOperation() {
        mBuilder = new AlertDialog.Builder(DrawingBoard.this);
        mBuilder.setTitle("SAVE IMAGE AS");
        final EditText fileNameInput = new EditText(DrawingBoard.this);
        fileNameInput.setHint(R.string.file_name);
        fileNameInput.setSingleLine(true);
        mBuilder.setView(fileNameInput);
        mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getCurrentFocus() != null) {
                    InputMethodManager keyboardManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    keyboardManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                final String fileName = fileNameInput.getText().toString();
                dialog.cancel();
                // Небольшая задержка во избежание обрезки холста при сохранении в галерею.
                if (fileName.toString().trim().length() > 0) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            saveImageToGallery(fileName);
                        }
                    }, 1000);
                } else {
                    Toast.makeText(DrawingBoard.this, "Image can't be saved without a name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mBuilder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        mDialog = mBuilder.create();
        mDialog.show();
        mDialog.setCanceledOnTouchOutside(false);
    }

    // Метод saveImageToGallery, который (не) подтверждает сохранение холста в галерею.
    public void saveImageToGallery(final String fileName) {
        mBuilder = new AlertDialog.Builder(DrawingBoard.this);
        mBuilder.setTitle("SAVE");
        mBuilder.setMessage("Save the drawing to Memory?");
        mBuilder.setPositiveButton("Yes", (dialog, which) -> {
            mPaintCanvas.setDrawingCacheEnabled(true);
            Bitmap bitmap = mPaintCanvas.getDrawingCache();
            String imgSaved = (new SavePhotoUtil(DrawingBoard.this)).saveToGallery(getContentResolver(), bitmap, fileName + ".png", "drawing");
            if (imgSaved != null) {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                File f = new File("file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                sendBroadcast(mediaScanIntent);
                Toast savedToast = Toast.makeText(getApplicationContext(),
                        "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                savedToast.show();
            } else {
                Toast unsavedToast = Toast.makeText(getApplicationContext(),
                        "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                unsavedToast.show();
            }
            mPaintCanvas.destroyDrawingCache();
            dialog.cancel();
        });
        mBuilder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        mDialog = mBuilder.create();
        mDialog.show();
        mDialog.setCanceledOnTouchOutside(false);
    }

    // Метод drawCircleOnBoard, который вызывается при выборе инструмента "Круг" из набора инструментов.
    // Запрашивает у пользователя радиус окружности.
    public void drawCircleOnBoard() {
        mBuilder = new AlertDialog.Builder(DrawingBoard.this);
        mBuilder.setTitle("Parameter!");
        final View view = mInflater.inflate(R.layout.shape_input, null);
        final EditText Circlerad = view.findViewById(R.id.circle_radius);
        LinearLayout RectangleWidthLayout = view.findViewById(R.id.rectangle_width_layout);
        LinearLayout RectangleHeightLayout = view.findViewById(R.id.rectangle_height_layout);
        RectangleHeightLayout.setVisibility(View.GONE);
        RectangleWidthLayout.setVisibility(View.GONE);
        mBuilder.setView(view);
        mBuilder.setPositiveButton("OK", (dialog, which) -> {
            if (!Circlerad.getText().toString().trim().isEmpty()) {
                mPaintCanvas.drawCircle(Integer.parseInt(Circlerad.getText().toString()));
                openColorDrawer();
            } else {
                Toast.makeText(DrawingBoard.this, "Circle radius missing!", Toast.LENGTH_SHORT).show();
                mPaintCanvas.setCurrentOperation(Constants.OPERATION_DRAW_PENCIL);
            }
        });
        mBuilder.setNegativeButton("Cancel", (dialog, which) -> {
            mPaintCanvas.setCurrentOperation(Constants.OPERATION_DRAW_PENCIL);
            dialog.cancel();
        });
        mDialog = mBuilder.create();
        mDialog.show();
    }

    // Метод drawRectangleOnBoard, который вызывается при выборе инструмента "Прямоугольник" из набора инструментов.
    // Запрашивает у пользователя длину и ширину прямоугольника.
    public void drawRectangleOnBoard() {
        mBuilder = new AlertDialog.Builder(DrawingBoard.this);
        if (mPaintCanvas.getCurrentOperation() == Constants.OPERATION_DRAW_RECTANGLE)
            mBuilder.setTitle("Rectangle Dimensions");
        else
            mBuilder.setTitle("Oval Dimensions");
        final View view = mInflater.inflate(R.layout.shape_input, null);
        LinearLayout CircleLayout = view.findViewById(R.id.circle_radius_layout);
        CircleLayout.setVisibility(View.GONE);
        final EditText RectWidth = view.findViewById(R.id.rectangle_width);
        final EditText RectHeight = view.findViewById(R.id.rectangle_heigth);
        mBuilder.setView(view);
        mBuilder.setPositiveButton("OK", (dialog, which) -> {
            if (!RectWidth.getText().toString().trim().isEmpty() && !RectHeight.getText().toString().trim().isEmpty()) {
                mPaintCanvas.drawRectangle(Integer.parseInt(RectWidth.getText().toString()),
                        Integer.parseInt(RectHeight.getText().toString()));
                openColorDrawer();
            } else {
                Toast.makeText(DrawingBoard.this, "Dimension missing!", Toast.LENGTH_SHORT).show();
                mPaintCanvas.setCurrentOperation(Constants.OPERATION_DRAW_PENCIL);
            }
        });
        mBuilder.setNegativeButton("Cancel", (dialog, which) -> {
            mPaintCanvas.setCurrentOperation(Constants.OPERATION_DRAW_PENCIL);
            dialog.cancel();
        });
        mDialog = mBuilder.create();
        mDialog.show();
    }

    // Метод getTextInputFromUser, который вызывается при выборе инструмента "Текст" из набора инструментов.
    // Запрашивает у пользователя текст, который нужно поместить на холст.
    public void getTextInputFromUser() {
        mBuilder = new AlertDialog.Builder(DrawingBoard.this);
        mBuilder.setTitle("Create Text Input!");
        final View view = mInflater.inflate(R.layout.text_input_layout, null);
        final EditText text = view.findViewById(R.id.input_text);
        final EditText textSize = view.findViewById(R.id.text_size);
        mBuilder.setView(view);
        mBuilder.setPositiveButton("OK", (dialog, which) -> {
            if (!text.getText().toString().trim().isEmpty() && !textSize.getText().toString().trim().isEmpty()) {
                mPaintCanvas.createTextBox(text.getText().toString().trim(), Integer.parseInt(textSize.getText().toString().trim()));
                openColorDrawer();
            } else {
                mPaintCanvas.setCurrentOperation(Constants.OPERATION_DRAW_PENCIL);
                Toast.makeText(DrawingBoard.this, "Input missing!", Toast.LENGTH_SHORT).show();
            }
        });
        mBuilder.setNegativeButton("Cancel", (dialog, which) -> {
            mPaintCanvas.setCurrentOperation(Constants.OPERATION_DRAW_PENCIL);
            dialog.cancel();
        });
        mDialog = mBuilder.create();
        mDialog.show();
    }

    // Метод loadImageFromGallery, который вызывается при выборе инструменты "Фото" из набора инструментов.
    // Запрашивает у пользователя изображение из галереи.
    public void loadImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.RESULT_LOAD_IMAGE);
    }

    // Переопределённый метод onActivityResult, который получает изображение из галереи.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RESULT_LOAD_IMAGE) {
            if (resultCode == RESULT_OK && data != null) {
                Uri uri = data.getData();
                String[] projections = new String[]{MediaStore.Images.Media.DATA};
                assert uri != null;
                Cursor cursor = getContentResolver().query(uri, projections, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();
                String filePath = cursor.getString(0);
                cursor.close();
                Bitmap background = BitmapFactory.decodeFile(filePath);
                mPaintCanvas.setCanvasBackground(background);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}