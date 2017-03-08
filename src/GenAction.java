import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.CollectionListModel;


/**
 * Created by romanrozin on 08/03/2017.
 */
public class GenAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiClass psiClass = getPsiClassFromContext(e);
        GenActionDialog genActionDialog = new GenActionDialog(psiClass);
        genActionDialog.show();
        if (genActionDialog.isOK()) {

            createCode(psiClass, genActionDialog.psiFieldCollectionListModel);


        }
    }

    private void createCode(PsiClass psiClass, CollectionListModel<String> psiFieldCollectionListModel) {
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
            @Override
            protected void run() throws Throwable {

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("public void comp(){\n");
                stringBuilder.append("// This is Sparta ! \n");
                stringBuilder.append("}");

                PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(getProject());
                PsiMethod psiMethod = psiElementFactory.createMethodFromText(stringBuilder.toString(), psiClass);
                psiClass.add(psiMethod);


            }
        }.execute();
    }


    @Override
    public void update(AnActionEvent e) {


        PsiClass psiClass = getPsiClassFromContext(e);
        e.getPresentation().setEnabled(psiClass != null);


    }

    private PsiClass getPsiClassFromContext(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);

        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);
        PsiClass psiClass = PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
        if (psiClass == null) {
            e.getPresentation().setEnabled(false);
            return null;

        }
        return psiClass;
    }
}
